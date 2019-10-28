package codes.spectrum.sources.core.client.b2badapter

import codes.spectrum.api.Severity
import codes.spectrum.data.extractor.createFromThrowable
import codes.spectrum.data.extractor.putInQuery
import codes.spectrum.data.extractor.queryMap
import codes.spectrum.message.Message
import codes.spectrum.source.ISourceIntegrationAdapter
import codes.spectrum.sources.ISourceHandler
import codes.spectrum.sources.SourceContext
import codes.spectrum.sources.config.IConfig
import codes.spectrum.source.Result
import codes.spectrum.source.exception.ExecutionException
import codes.spectrum.api.SourceState
import codes.spectrum.data.*
import kotlinx.coroutines.runBlocking
import net.logstash.logback.argument.StructuredArguments
import org.slf4j.Logger

/*
Базовый Адаптер для встраивания источников в sourse-app.
Является и ISourceIntegrationAdapter и ISourceHandler одновременно
 */
abstract class BaseB2bIntegrationAdapter<Q, R>(
        /*
        Клиент. Основная логика получения результата заложена внутри него.
         */
        val client: ISourceHandler<SourceContext<Q, R>>,
        var logger : Logger,
        /*
        Мета информация по работе источника
         */
        var descriptor: SourceDescriptor
): ISourceHandler<SourceContext<Q, R>>, ISourceIntegrationAdapter<R, String, Result<R, String>> {

    /*
    Получение контекста для конкретного адаптера. Реализуется непосредственно в наследнике
     */
    abstract fun getContext(message: Message): SourceContext<Q, R>

    /*
    Метод обертка для аналогичного метода клиента. Позволяет добавить пре и пост обработку.
    В простейшем случае не требует переопределения
     */
    override suspend fun execute(context: SourceContext<Q, R>, config: IConfig) {
        client.execute(context, config)
    }

    override fun isApplicable(code: String) = code == descriptor.code

    /*
        Основной метод адаптера. В простейшем случае не требует переопределения
        */
    override fun execute(message: Message): Result<R, String> {
        return try {
            val countTries = getCountTries(message)

            val useSource = descriptor.useSource(countTries)

            logger.debug("Count tries for query is $countTries")

            if(!useSource){
                return Result.create(ExecutionException("Exceeded the number of attempts. ($countTries from ${getCountTriesLimit(descriptor)})"), SourceState.ABORTED)
            }

            logger.info("${descriptor.code} received request")

            val context = getContext(message)

            logger.debug("Query body: {}", StructuredArguments.value("queryBody", context.query.query))

            if(context.result.status == SourceState.SKIP_QUERY){
                return Result.create(resultFromData(context.result.data)!!, context.result.status)
            }

            runBlocking {
                execute(context)
            }

            context.options[MESSAGE] = message
            context.options[COUNT_TRIES] = countTries
            context.options[SOURCE_DESCRIPTOR] = descriptor

            createResultFromContext(context)
        } catch (throwable: Throwable){
            Result.createFromThrowable(throwable, logger, descriptor.code, SourceState.INTEGRATION_ERROR)
        }
    }

    /*
    Получение стогого типа из Any для result.data
     */
    abstract fun resultFromData(data: Any?): R?

    /*
    Отправка в message requestId для асинхроного запроса
     */
    private fun putInQueryId(message: Message, fieldName: String, value: String?){
        if(value != null){
            message.putInQuery(fieldName, value)
        }
    }

    /*
    Получение названия поля для requestId, отличного от дефолтного
     */
    open fun getFieldNameForRequestId(): String = REQUEST_ID

    /*
    Получения значения для requestId из result.data
     */
    open fun getValueForRequestId(context: SourceContext<Q, R>): String? = null

    /*
    Формирование конечного результата. В простейшем случае не требует переопределения.
     */
    open fun createResultFromContext(context: SourceContext<Q, R>): Result<R, String> {
        val sourceResultObj = context.result.data
        val status = context.result.status
        val message = context.getFromOptions<Message>(MESSAGE)
        var countTries = context.getFromOptions<Int>(COUNT_TRIES)
        val sourceDescriptor = context.getFromOptions<SourceDescriptor>(SOURCE_DESCRIPTOR)

        return when {
            /*
            Обработка статусов с возможностью дополнительного запроса (дозапрос при асинхронном источнике, либо уход на delay)
             */
            status.recoverable -> {
                val result:Result<R, String> = if(status == SourceState.PROGRESS) {
                    Result.create(sourceDescriptor.timeoutForProgress, message.header.uid, false, status)
                } else {
                    message.putInQuery(SourceLimit.COUNT_TRIES, ++countTries)
                    Result.create(sourceDescriptor.timeout, message.header.uid, true, status)
                }

                result.apply { this.data = resultFromData(sourceResultObj) }

                if(result.stateV2() == SourceState.PROGRESS){
                    putInQueryId(message, getFieldNameForRequestId(), getValueForRequestId(context))
                }

                result
            }
            /*
            Обработка статусов с успешным значением
             */
            status.isOk() -> {
                Result.create(resultFromData(context.result.data)!!, status)
            }
            else -> {
                val result: Result<R, String> = Result.createFromThrowable(context.result.error ?: ExecutionException("Unknown error"),
                        logger, descriptor.code, status)
                result.apply { this.data = resultFromData(sourceResultObj) }
                result
            }
        }
    }

    open fun SourceState.isOk() = this.severity == Severity.OK || this.severity == Severity.HINT

    private fun getCountTriesLimit(sourceDescriptor: SourceDescriptor) = sourceDescriptor.limits.firstOrNull { it.name == SourceLimit.COUNT_TRIES }?.value ?: DEFAULT_COUNT_TRIES

    private fun getCountTries(msg: Message): Int = msg.queryMap()[SourceLimit.COUNT_TRIES]?.toString()?.toInt() ?: 0

    companion object {
        const val DEFAULT_COUNT_TRIES: Int = 10
        const val MESSAGE = "message"
        const val COUNT_TRIES = "countTries"
        const val SOURCE_DESCRIPTOR = "sourceDescriptor"
        const val REQUEST_ID = "requestId"
    }
}
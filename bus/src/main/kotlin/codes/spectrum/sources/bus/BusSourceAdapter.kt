package codes.spectrum.sources.bus

import codes.spectrum.bus.builder.BusQueueHandlerContext
import codes.spectrum.message.queue.toMessage
import codes.spectrum.serialization.json.Json
import codes.spectrum.sources.ISourceHandler
import codes.spectrum.sources.SourceContext
import codes.spectrum.sources.bus.exceptions.IllegalMessageException
import codes.spectrum.api.Severity
import codes.spectrum.api.SourceState
import codes.spectrum.sources.config.GlobalConfig
import codes.spectrum.sources.config.IConfig
import codes.spectrum.sources.safeExecute
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import kotlin.reflect.full.createInstance

abstract class BusSourceAdapter<Q, R>(
    protected val internalSource: ISourceHandler<out SourceContext<Q, R>>,
    protected val contextClass: Class<out SourceContext<Q, R>>,
    /**
     * Функция для вычисления какой будет следующий класс делея для переоправки сообщения
     * @param currentDelayClass String? - с каким делей классом было оправлено текущее сообщение
     * @param currentDelayCount Int - Сколько раз сообщение уходило в делей
     * @param status SourceState? - Статус последнего делея
     *
     * @return String? с каким делеем сообщение будет переотправлено,
     * если функция вернет null, то делея не произойдет
     * если функция вернет "ERROR", то сообщение попадет в очередь с ошибками
     */
    protected val getNextDelayClass: (
        currentDelayClass: String?,
        currentDelayCount: Int,
        status: SourceState?
    ) -> String? = { currentDelayClass, currentDelayCount, status ->
        when (currentDelayClass) {
            null -> DELAY_JUST5
            DELAY_JUST5 -> DELAY_SHORTEST
            DELAY_SHORTEST -> DELAY_SHORT
            DELAY_SHORT -> DELAY_MEDIUM
            DELAY_MEDIUM -> DELAY_LONG
            DELAY_LONG -> DELAY_LONGEST
            DELAY_LONGEST -> DELAY_ERROR
            else -> DELAY_ERROR
        }
    }
) {
    val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun execute(context: BusQueueHandlerContext, config: IConfig = GlobalConfig): SourceState {
        val internalContext = contextClass.kotlin.createInstance()
        try {
            prepareContextFromRabbitMessage(internalContext, context)
            internalSource.safeExecute(internalContext, config)
            when {
                internalContext.result.status.recoverable
                    || internalContext.result.status in setOf(SourceState.PROGRESS) -> context.delay(internalContext.result.status)
                internalContext.result.status.severity == Severity.ERROR -> context.error()
            }
        } catch (e: Throwable) {
            context.error()
            internalContext.result.status = SourceState.SERVICE_ERROR
        }
        return internalContext.result.status
    }

    open suspend fun prepareContextFromRabbitMessage(context: SourceContext<Q, R>, busContext: BusQueueHandlerContext) {
        val request = busContext.message.toMessage().body.options["request"]
            ?: throw IllegalMessageException("Message.body.options don't have request")
        val requestJson = Json.stringify(request)
        logger.debug("Got query object: $requestJson")
        context.query = Json.read(requestJson, context.query.javaClass)
    }

    private fun BusQueueHandlerContext.error() {
        navigator.send(
            serviceName ?: throw RuntimeException("Service name not specified"),
            serviceError ?: throw RuntimeException("Error output not specified"),
            message)
    }

    private fun BusQueueHandlerContext.delay(status: SourceState) {
        val currentDelayClass = message.getCurrentDelayClass()
        val currentDelayCount = message.getCurrentDelayCount()

        when (val nextDelayClass = getNextDelayClass(currentDelayClass, currentDelayCount, status)) {
            DELAY_ERROR -> error()
            null -> {
            }
            else -> {
                message.setDelayCount(currentDelayCount + 1)
                navigator.delay(
                    serviceName ?: throw RuntimeException("Service name not specified"),
                    serviceInput ?: throw RuntimeException("Main input not specified"),
                    message,
                    nextDelayClass)
            }
        }
    }

    private fun Message.getCurrentDelayClass() = this.messageProperties.headers["s-delay"]?.toString()
    private fun Message.getCurrentDelayCount() = this.messageProperties.headers["delay-count"]?.toString()?.toInt() ?: 0
    private fun Message.setDelayCount(count: Int) = this.messageProperties.headers.set("delay-count", count)

    companion object {
        const val DELAY_JUST5 = "JUST5"
        const val DELAY_SHORTEST = "SHORTEST"
        const val DELAY_SHORT = "SHORT"
        const val DELAY_MEDIUM = "MEDIUM"
        const val DELAY_LONG = "LONG"
        const val DELAY_LONGEST = "LONGEST"
        const val DELAY_ERROR = "ERROR"
    }
}


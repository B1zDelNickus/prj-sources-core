package codes.spectrum.sources.core.rest

import codes.spectrum.api.SourceState
import codes.spectrum.serialization.json.Json
import codes.spectrum.sources.*
import codes.spectrum.sources.core.model.IResponseConfigContext
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondOutputStream
import io.ktor.util.pipeline.PipelineContext
import org.slf4j.LoggerFactory
import java.io.InvalidClassException
import kotlin.reflect.full.createInstance

abstract class RestSourceAdapter<Q, R>(
    protected val internalSource: ISourceHandler<out SourceContext<Q, R>>,
    protected val contextClass: Class<out SourceContext<Q, R>>
) {
    val logger = LoggerFactory.getLogger(this::class.java)
    
    /**
     * Ищет конфигурацию ответного запроса во внутреннем контексте, если нет - возвращает стандартную
     */
    open fun getResponseConfig(internalContext: SourceContext<Q, R>) =
        if (internalContext is IResponseConfigContext) {
            internalContext.responseConfig
        } else SourceResponseConfig.DEFAULT
    
    
    /**
     * Исполняет запрос ktor.
     * Внутренний источник исполняет его с внутренним контекстом.
     * Внутренний контекст собирается из http-запроса ktor, и может затем быть модифицирован.
     *
     * @param context Внешний контекст ktor
     * @param block Функция модификации собранного из http-запроса внутреннего контекста
     * @return Состояние внутреннего контекста после исполнения запроса ([[SourceState]])
     */
    suspend fun execute(context: PipelineContext<*, ApplicationCall>, block: SourceContext<Q, R>.() -> Unit = {}): SourceState {
        var internalcontext = contextClass.kotlin.createInstance()
        return try {
            prepareContextFromHttpRequest(internalcontext, context)
            internalcontext.block()
            
            if (getResponseConfig(internalcontext).respondAsStream()) {
                executeStream(context, internalcontext)
            } else {
                executeContext(context, internalcontext)
            }
        } catch (e: Throwable) {
            internalcontext.result.status = SourceState.SERVICE_ERROR
            context.call.respond(HttpStatusCode.fromValue(internalcontext.resolveHttpStatus()), internalcontext)
            internalcontext.result.status
        }
    }
    
    /**
     * Ответить на запрос, вернув internalContext (стандартное поведение)
     */
    protected suspend fun executeContext(context: PipelineContext<*, ApplicationCall>, internalcontext: SourceContext<Q, R>): SourceState {
        try {
            internalSource.safeExecute(internalcontext)
        } catch (e: Throwable) {
            internalcontext.result.status = SourceState.SERVICE_ERROR
        }
        context.call.respond(HttpStatusCode.fromValue(internalcontext.resolveHttpStatus()), internalcontext)
        return internalcontext.result.status
    }
    
    /**
     * Ответить на запрос потоковыми данными.
     * Поток ответа передаётся во внутренний контекст, внутри вызова внутреннего конвейера должны быть записаны данные.
     * contentType ответа будет вычислен из responseConfig из интерфейса [[IResponseConfigContext]].
     * internalContext должен наследовать этот интерфейс, иначе будет InvalidClassException.
     */
    protected suspend fun executeStream(context: PipelineContext<*, ApplicationCall>, internalcontext: SourceContext<Q, R>): SourceState {
        val responseConfig = getResponseConfig(internalcontext)
        internalcontext as? IResponseConfigContext ?: throw InvalidClassException(
            "To work with stream, you must apply additional interface IResponseConfigContext to ${internalcontext::class}"
        )
        context.call.respondOutputStream(ContentType.parse(responseConfig.contentType)) {
            try {
                internalcontext.outputStream(this)
                internalSource.safeExecute(internalcontext)
            } catch (e: Throwable) {
                internalcontext.result.status = SourceState.SERVICE_ERROR
                logger.error("Error in stream request handling for contentType {} with {}", responseConfig.contentType, e)
                writer(Charsets.UTF_8)
                    .write("Произошла внутренняя ошибка, обратитесь к системному администратору")
            }
        }
        return internalcontext.result.status
    }
    
    open suspend fun prepareContextFromHttpRequest(context: SourceContext<Q, R>, pipeline: PipelineContext<*, ApplicationCall>) {
        val input = InputDataParser.Instance.parseInput(context.query.javaClass, pipeline, SourceQuery<*>::debug.name, SourceQuery<*>::query.name)
        logger.debug("Got query object: ${Json.stringify(input)}")
        context.query = input
    }
}




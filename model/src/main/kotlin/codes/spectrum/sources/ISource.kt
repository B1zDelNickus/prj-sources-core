package codes.spectrum.sources

import codes.spectrum.sources.config.GlobalConfig
import codes.spectrum.sources.config.IConfig
import codes.spectrum.api.SourceState
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import org.slf4j.Logger
import org.slf4j.event.Level

interface ISourceHandler<TContext:SourceContext<*,*>> {
    suspend fun execute(context: TContext, config: IConfig = GlobalConfig)
}

/**
 * Инварианта безопасного вызова источника, должна использоваться в интеграционных контекстах
 */
suspend fun <TContext:SourceContext<*,*>> ISourceHandler<TContext>.safeExecute(
    context: SourceContext<*, *>,
    config: IConfig = GlobalConfig,
    timeout: Long? = null,
    errorStatus: SourceState = SourceState.SOURCE_ERROR,
    logger: Logger? = null,
    normalLogLevel:Level = Level.TRACE
) {
    try {
        logger.write("Start call ${this} with ${context.query}",normalLogLevel)
        val workingTimeout = timeout ?: context.query.timeout

        if (workingTimeout > 0) {
            withTimeout(workingTimeout) {
                checkDebug(context)
                execute(context as TContext, config)
            }
        } else {
            checkDebug(context)
            execute(context as TContext, config)
        }
        logger.write("Sucessfully call ${this} with ${context.query}",normalLogLevel)
    } catch (e: TimeoutCancellationException) {
        logger.write("Timeout call ${this} with ${context.query}",Level.WARN)
        context.result.status = SourceState.TIME_OUT
    } catch (e: Throwable) {
        logger.write("Error call ${this} with ${context.query}",Level.ERROR)
        context.result.status = errorStatus
        context.result.error = e
    } finally {
        if(context.result.status==SourceState.NONE){
            context.result.status = if(null==context.result.error)SourceState.OK else errorStatus
        }
    }
}

private suspend fun <TQuery, TResult> checkDebug(context: SourceContext<TQuery, TResult>) {
    context.query.debug?.let {
        if (it.sourceDelay > 0) {
            delay(it.sourceDelay)
        }
        if (it.throwError) {
            throw Exception("Debug imitate error from source", Exception("Debug imitate internal error"))
        }
    }
}

fun Logger?.write(message:String, level: Level = Level.INFO){
    this?.let {
        when (level) {
            Level.TRACE -> trace(message)
            Level.DEBUG -> debug(message)
            Level.WARN -> warn(message)
            Level.ERROR -> error(message)
            Level.INFO -> info(message)
        }
    }
}



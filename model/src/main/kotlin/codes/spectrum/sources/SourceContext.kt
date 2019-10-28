package codes.spectrum.sources

import codes.spectrum.sources.config.EnvProxy
import codes.spectrum.sources.config.IConfig
import codes.spectrum.api.Severity
import codes.spectrum.api.SourceState
import codes.spectrum.api.exceptions.FieldNotFoundInOptionsException
import codes.spectrum.api.exceptions.FieldWithTypeNotFoundInOptionsException
import java.io.OutputStream


open class SourceContext<TQuery,TResult> (
    var query: SourceQuery<TQuery>,
    var result: SourceResult<TResult>,
    var options: MutableMap<String, Any> = mutableMapOf()
) {

    val statistics: ContextStatistics = ContextStatistics()

    fun resolveHttpStatus():Int {
        if (query.errorHttpCode <= 0) return result.status.code
        if (result.status.severity != Severity.ERROR) return 200
        return query.errorHttpCode
    }

    /**
     * Добавляет формализованный объект-ошибку к контексту для учета и последующих действий
     */
    open fun addError(error: Throwable, env: IConfig = EnvProxy.Instance) {
        result.error = error
        incrementError(env)
    }

    open fun hasErrors(): Boolean {
        return result.error != null
    }

    open fun getErrors(env: IConfig = EnvProxy.Instance): List<Throwable> {
        return if (result.error != null)
            listOf(result.error as Throwable)
        else
            listOf()
    }

    open fun incrementTotal(env: IConfig = EnvProxy.Instance) {
        statistics.total.incrementAndGet()
    }

    open fun incrementSuccess(env: IConfig = EnvProxy.Instance) {
        statistics.successful.incrementAndGet()
        incrementTotal()
    }

    open fun incrementError(env: IConfig = EnvProxy.Instance) {
        statistics.error.incrementAndGet()
        incrementTotal()
    }

    inline fun <reified T> getFromOptionsOrNull(fieldName: String): T? {
        return try {
            getFromOptions<T>(fieldName)
        } catch (exc: Throwable){
            null
        }
    }

    inline fun <reified T> getFromOptions(fieldName: String, isReturnNull: Boolean = false): T {
        val obj = this.options[fieldName]
        return when {
            obj != null && obj is T -> obj
            isReturnNull -> throw NullPointerException("isReturnNull flag on")
            obj != null -> throw FieldWithTypeNotFoundInOptionsException(fieldName, T::class.java.canonicalName)
            else -> throw  FieldNotFoundInOptionsException(fieldName)
        }
    }
}
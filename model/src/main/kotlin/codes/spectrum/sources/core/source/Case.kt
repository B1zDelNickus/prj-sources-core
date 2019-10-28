package codes.spectrum.sources.core.source

import codes.spectrum.CheckResult
import codes.spectrum.CheckState
import codes.spectrum.sources.DebugMode
import codes.spectrum.sources.ISourceHandler
import codes.spectrum.sources.SourceContext
import codes.spectrum.sources.SourceResult
import codes.spectrum.sources.core.model.IQuery

/**
 * Класс, описывающий кейс
 * Используется в интерфейсе ui.html и в тестах
 * Также может использовать для подготовки фейков
 */
open class Case (
    /**
     * Наименование кейса
     */
    val name: String = DEFAULT_NAME,

    /**
     * Индекс кейса (необходим для сортировки)
     */
    val index: Int = DEFAULT_INDEX,

    /**
     * Запрос
     */
    val query: IQuery? = DEFAULT_QUERY,

    /**
     * Значение поля DebugMode кейса
     */
    val debug: DebugMode = DEFAULT_DEBUG_MODE,

    /**
     * Значение timeout
     */
    val timeout: Long = DEFAULT_TIMEOUT,

    /**
     * Описание кейса
     */
    val description: String = DEFAULT_DESCRIPTION,

    /**
     * Фейковый результат кейса
     */
    val fakeResult: SourceResult<*>? = DEFAULT_FAKE_RESULT,

    /**
     * Флаг тестирования: true - кейс тестируется, false - нет
     */
    val isTest: Boolean = DEFAULT_IS_TEST,

    /**
     * Флаг добавления в UI: true - кейс добавляется в UI, false - нет
     */
    val isForUI: Boolean = DEFAULT_IS_FOR_UI,

    /**
     * Флаг кейса по умолчанию
     */
    val isDefault: Boolean = DEFAULT_IS_DEFAULT,

    /**
     * Функция валидации обработки запроса (необходимо перегружать для тестируемых кейсов)
     */
    val validate: (SourceResult<*>) -> CheckResult = DEFAULT_VALIDATE,

    /**
     * Функция выполнения запроса (после которой будет проверяться функция validate)
     */
    val execute: ((ISourceHandler<*>, SourceContext<*, *>) -> Unit)? = DEFAULT_EXECUTE
) {
    class Builder {
        /**
         * Наименование кейса
         */
        var name: String = DEFAULT_NAME

        /**
         * Индекс кейса (необходим для сортировки)
         */
        var index: Int = DEFAULT_INDEX

        /**
         * Запрос
         */
        var query: IQuery? = DEFAULT_QUERY

        /**
         * Значение поля DebugMode кейса
         */
        var debugMode: DebugMode = DEFAULT_DEBUG_MODE

        /**
         * Значение timeout
         */
        var timeout: Long = DEFAULT_TIMEOUT

        /**
         * Описание кейса
         */
        var description: String = DEFAULT_DESCRIPTION

        /**
         * Фейковый результат кейса
         */
        var fakeResult: SourceResult<*>? = DEFAULT_FAKE_RESULT

        /**
         * Флаг тестирования: true - кейс тестируется, false - нет
         */
        var isTest: Boolean = DEFAULT_IS_TEST

        /**
         * Флаг добавления в UI: true - кейс добавляется в UI, false - нет
         */
        var isForUI: Boolean = DEFAULT_IS_FOR_UI

        /**
         * Флаг кейса по умолчанию
         */
        var isDefault: Boolean = DEFAULT_IS_DEFAULT

        /**
         * Функция валидации обработки запроса (необходимо перегружать для тестируемых кейсов)
         */
        var validate: (SourceResult<*>) -> CheckResult = DEFAULT_VALIDATE

        /**
         * Функция выполнения запроса (после которой будет проверяться функция validate)
         */
        var execute: ((ISourceHandler<*>, SourceContext<*, *>) -> Unit)? = DEFAULT_EXECUTE

        fun build(): Case =
            Case(
                name = name,
                index = index,
                query = query,
                debug = debugMode,
                timeout = timeout,
                description = description,
                fakeResult = fakeResult,
                isTest = isTest,
                isForUI = isForUI,
                isDefault = isDefault,
                validate = validate,
                execute = execute
            )
    }

    companion object {
        /**
         * name по умолчанию
         */
        const val DEFAULT_NAME = ""

        /**
         * index по умолчанию
         */
        const val DEFAULT_INDEX = 0

        /**
         * Запрос по умолчанию
         */
        val DEFAULT_QUERY = null

        /**
         * debugMode по умолчанию
         */
        val DEFAULT_DEBUG_MODE = DebugMode()

        /**
         * timeout по умолчанию
         */
        const val DEFAULT_TIMEOUT = 0L

        /**
         * description по умолчанию
         */
        const val DEFAULT_DESCRIPTION = ""

        /**
         * fakeResult по умолчанию
         */
        val DEFAULT_FAKE_RESULT = null

        /**
         * isTest по умолчанию
         */
        const val DEFAULT_IS_TEST = false

        /**
         * isForUI по умолчанию
         */
        const val DEFAULT_IS_FOR_UI = true

        /**
         * isDefault по умолчанию
         */
        const val DEFAULT_IS_DEFAULT = false

        /**
         * validate по умолчанию
         */
        val DEFAULT_VALIDATE = fun(_: SourceResult<*>): CheckResult = CheckResult(state = CheckState.Ok)

        /**
         * execute по умолчанию
         */
        val DEFAULT_EXECUTE = null

        operator fun invoke(
            name: String = DEFAULT_NAME,
            index: Int = DEFAULT_INDEX,
            query: IQuery? = DEFAULT_QUERY,
            debugMode: DebugMode = DEFAULT_DEBUG_MODE,
            timeout: Long = DEFAULT_TIMEOUT,
            description: String = DEFAULT_DESCRIPTION,
            fakeResult: SourceResult<*>? = DEFAULT_FAKE_RESULT,
            isTest: Boolean = DEFAULT_IS_TEST,
            isForUI: Boolean = DEFAULT_IS_FOR_UI,
            isDefault: Boolean = DEFAULT_IS_DEFAULT,
            validate: (SourceResult<*>) -> CheckResult = DEFAULT_VALIDATE,
            execute: ((ISourceHandler<*>, SourceContext<*, *>) -> Unit)? = DEFAULT_EXECUTE,
            body: Builder.() -> Unit) =
            Builder().apply {
                this.name = name
                this.index = index
                this.query = query
                this.debugMode = debugMode
                this.timeout = timeout
                this.description = description
                this.fakeResult = fakeResult
                this.isTest = isTest
                this.isForUI = isForUI
                this.isDefault = isDefault
                this.validate = validate
                this.execute = execute
                body()
            }.build()

        val Default by lazy { Case() }
    }
}
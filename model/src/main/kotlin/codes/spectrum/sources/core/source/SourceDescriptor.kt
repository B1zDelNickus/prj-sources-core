package codes.spectrum.sources.core.source

import codes.spectrum.api.SourceState
import codes.spectrum.sources.*
import codes.spectrum.sources.core.SourceDefinition
import codes.spectrum.sources.core.model.IQuery
import codes.spectrum.sources.core.source.cases.ErrorCase
import codes.spectrum.sources.core.source.cases.NoneCase
import codes.spectrum.sources.core.source.cases.TimeoutCase
import codes.spectrum.sources.core.source.systems.DevRestSystem
import codes.spectrum.sources.core.source.systems.RelativeRestSystem
import codes.spectrum.sources.core.source.systems.LocalRestSystem
import codes.spectrum.sources.core.source.systems.ProdRestSystem
import java.io.File

/**
 * Класс, описывающий источник
 */
data class SourceDescriptor(

    /**
     * Описание источника (есть поддержка Markdown)
     */
    val description: String = DEFAULT_DESCRIPTION,

    /**
     * Список кейсов
     */
    val cases: List<Case> = DEFAULT_CASES,

    /**
     * Список REST-систем
     */
    val systems: List<RestSystem> = DEFAULT_SYSTEMS,

    /**
     * Метод преобразования описания в html-разметку
     */
    val caseDescriptionConverter: (String) -> String = DEFAULT_CASE_DESCRIPTION_CONVERTER,

    /**
     * Список кодов ответа, на который возвращается ответ
     */
    val goodCodes: List<SourceState> = DEFAULT_GOOD_CODES

) {

    /**
     * Код кейса по умолчанию
     */
    val defaultCaseCode: String by lazy {
        val defaultCase = cases.firstOrNull { it.isDefault } ?: cases.firstOrNull()
        if (defaultCase != null)
            getCodeByCase(defaultCase)
        else
            DEFAULT_CASE_CODE
    }

    /**
     * Класс source
     */
    @delegate:Transient
    val sourceClazz: Class<*> by lazy { Class.forName(SourceDefinition.Instance.sourceClazzName) }

    /**
     * Класс context
     */
    @delegate:Transient
    val contextClazz: Class<*> by lazy { Class.forName(SourceDefinition.Instance.contextClazzName) }

    /**
     * Класс request
     */
    @delegate:Transient
    val requestClazz: Class<*> by lazy { Class.forName(SourceDefinition.Instance.requestClazzName) }

    /**
     * Класс result
     */
    @delegate:Transient
    val resultClazz: Class<*> by lazy { Class.forName(SourceDefinition.Instance.resultClazzName) }

    /**
     * Класс запроса
     */
    @delegate:Transient
    val queryClazz: Class<*> by lazy { Class.forName(SourceDefinition.Instance.queryClazzName) }

    /**
     * Список параметров запроса
     */
    val queryList: List<Parameter> by lazy { ParameterFactory.getParameters(queryClazz) }

    /**
     * Метод получения UI кейсов
     */
    val uiCases get(): List<Case> =
        standardCases + cases.filter { it.isForUI }.sortedBy { it.index }

    /**
     * Метод получения тестируемых кейсов
     */
    val testCases get(): List<Case> =
        cases.filter { it.isTest }

    /**
     * Метод получения кейса по названию его объекта (caseCode)
     */
    fun getCaseByCode(caseCode: String): Case? =
        (standardCases + cases).firstOrNull { getCodeByCase(it) == caseCode }

    /**
     * Метод получения caseCode кейса
     */
    fun getCodeByCase(case: Case): String =
        case.javaClass.simpleName

    /**
     * Метод получения списка пар (имя, код) кейсов - необходимо для генерации ui.html
     */
    val uiCasesPairs get(): List<Pair<String, String>> =
        uiCases.map { Pair(it.name, getCodeByCase(it)) }

    /**
     * Метод получения списка REST-площадок
     */
    val restSystems get(): List<RestSystem> =
        (standardSystems + systems).sortedBy { it.index }

    /**
     * Метод получения кода REST-площадки
     */
    fun getCodeByRestSystem(system: RestSystem): String =
        system.javaClass.simpleName

    /**
     * Метод получения списка пар (имя, код) REST-систем - необходимо для генерации ui.html
     */
    val restSystemsPairs get(): List<Pair<String, String>> =
        restSystems.map { Pair(it.name, getCodeByRestSystem(it)) }

    /**
     * Метод создания провайдера
     */
    fun createSource(): ISourceHandler<SourceContext<Any, Any>> =
        sourceClazz.getConstructor().newInstance() as ISourceHandler<SourceContext<Any, Any>>

    /**
     * Метод создания контекста
     */
    fun createContext(
        request: SourceQuery<Any>,
        result: SourceResult<Any> = createResult()
    ): SourceContext<Any, Any> =
        contextClazz.getConstructor(requestClazz, resultClazz).newInstance(request, result) as SourceContext<Any, Any>

    /**
     * Метод создания запроса
     */
    fun createRequest(
        query: IQuery = createQuery(),
        debug: DebugMode = DebugMode(),
        caseCode: String = ""
    ): SourceQuery<Any> = requestClazz
        .getConstructor(queryClazz, DebugMode::class.java, String::class.java)
        .newInstance(query, debug, caseCode) as SourceQuery<Any>

    /**
     * Метод создания результата
     */
    fun createResult(): SourceResult<Any> = resultClazz.getConstructor().newInstance() as SourceResult<Any>

    /**
     * Метод создания query
     */
    fun createQuery(): IQuery = queryClazz.getConstructor().newInstance() as IQuery

    class Builder {

        /**
         * Описание источника (есть поддержка Markdown)
         */
        var description: String = DEFAULT_DESCRIPTION

        /**
         * Список кейсов
         */
        var cases: MutableList<Case> = mutableListOf(*DEFAULT_CASES.toTypedArray())

        /**
         * Список REST-систем
         */
        var systems: MutableList<RestSystem> = mutableListOf(*DEFAULT_SYSTEMS.toTypedArray())

        /**
         * Метод преобразования описания в html-разметку
         */
        var caseDescriptionConverter: (String) -> String = DEFAULT_CASE_DESCRIPTION_CONVERTER

        /**
         * Список кодов ответа, на который возвращается ответ
         */
        var goodCodes: MutableList<SourceState> = mutableListOf(*DEFAULT_GOOD_CODES.toTypedArray())

        /**
         * Позволяет добавлять уникальные кейсы
        */
        operator fun Case.unaryPlus() {
            if (!cases.contains(this)) {
                cases.add(this)
            }
        }

        /**
         * Позволяет добавлять уникальные REST-площадки
         */
        operator fun RestSystem.unaryPlus() {
            if (!systems.contains(this)) {
                systems.add(this)
            }
        }

        /**
         * Позволяет добавлять код результата
         */
        operator fun SourceState.unaryPlus() {
            if (!goodCodes.contains(this)) {
                goodCodes.add(this)
            }
        }

        fun build(): SourceDescriptor =
            SourceDescriptor(
                description = description,
                cases = cases.toList(),
                systems = systems.toList(),
                caseDescriptionConverter = caseDescriptionConverter,
                goodCodes = goodCodes.toList()
            )
    }

    companion object {

        /**
         * Файл, содержащий описание источника (по умолчанию)
         */
        private const val DEFAULT_SOURCE_DESCRIPTION_PATH = "../SourceDescription.md"

        /**
         * Ключ переменной среды, задающей путь до описательного файла источника
         */
        private const val SOURCE_DESCRIPTION_FILE_ENV = "SOURCE_DESCRIPTION_FILE"

        /**
         * Метод чтения описания источника из файла
         */
        private fun getSourceDescription(): String =
            try {
                val path = System.getenv(SOURCE_DESCRIPTION_FILE_ENV) ?: DEFAULT_SOURCE_DESCRIPTION_PATH
                File(path).readText()
            }
            catch (error: Throwable) { "" }

        /**
         * Код пользовательского кейса
         */
        const val NONE_CASE_CODE: String = "NoneCase"

        /**
         * Стандартные кейсы
         */
        val standardCases: List<Case> by lazy {
            listOf(
                NoneCase,
                TimeoutCase,
                ErrorCase
            )
        }

        /**
         * Стандартные REST-системы
         */
        val standardSystems by lazy {
            listOf(
                DevRestSystem,
                ProdRestSystem,
                LocalRestSystem,
                RelativeRestSystem
            )
        }

        /**
         * description по умолчанию
         */
        val DEFAULT_DESCRIPTION: String = getSourceDescription()

        /**
         * cases по умолчанию
         */
        val DEFAULT_CASES: List<Case> = emptyList()

        /**
         * systems по умолчанию
         */
        val DEFAULT_SYSTEMS: List<RestSystem> = emptyList()

        /**
         * defaultCaseCode по умолчанию
         */
        const val DEFAULT_CASE_CODE: String = NONE_CASE_CODE

        /**
         * htmlDescriptionConverter по умолчанию
         */
        val DEFAULT_CASE_DESCRIPTION_CONVERTER: (String) -> String =
            fun (description: String): String = description

        /**
         * codesList по умолчанию
         */
        val DEFAULT_GOOD_CODES: List<SourceState> = listOf(SourceState.OK)

        operator fun invoke(
            description: String = DEFAULT_DESCRIPTION,
            cases: List<Case> = DEFAULT_CASES,
            systems: List<RestSystem> = DEFAULT_SYSTEMS,
            caseDescriptionConverter: (String) -> String = DEFAULT_CASE_DESCRIPTION_CONVERTER,
            goodCodes: List<SourceState> = DEFAULT_GOOD_CODES,
            body: Builder.() -> Unit) =
            Builder().apply {
                this.description = description
                this.cases.addAll(cases)
                this.systems.addAll(systems)
                this.caseDescriptionConverter = caseDescriptionConverter
                this.goodCodes.addAll(goodCodes)
                body()
            }.build()

        val Default by lazy {
            SourceDescriptor()
        }
    }
}
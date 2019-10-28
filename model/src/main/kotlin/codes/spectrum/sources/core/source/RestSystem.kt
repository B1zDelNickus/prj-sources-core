package codes.spectrum.sources.core.source

/**
 * Класс, описывающий REST-площадку
 * Используется в интерфейсе ui.html
 */
open class RestSystem(
    /**
     * Наименование системы в UI
     */
    val name: String = DEFAULT_NAME,

    /**
     * Ссылка для доступа к системе
     */
    val url: String = DEFAULT_URL,

    /**
     * Индекс кейса (необходим для сортировки)
     */
    val index: Int = DEFAULT_INDEX
) {
    class Builder {
        /**
         * Наименование системы в UI
         */
        var name: String = DEFAULT_NAME

        /**
         * Ссылка для доступа к системе
         */
        var url: String = DEFAULT_URL

        /**
         * Индекс кейса (необходим для сортировки)
         */
        var index: Int = DEFAULT_INDEX

        fun build(): RestSystem =
            RestSystem(
                name = name,
                index = index,
                url = url
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
         * url по умолчанию
         */
        const val DEFAULT_URL = ""

        operator fun invoke(
            name: String = DEFAULT_NAME,
            index: Int = DEFAULT_INDEX,
            url: String = DEFAULT_URL,
            body: Builder.() -> Unit) =
            Builder().apply {
                this.name = name
                this.index = index
                this.url = url
                body()
            }.build()

        val Default by lazy { RestSystem() }
    }
}
package codes.spectrum.sources.core.client.markdown

import codes.spectrum.utils.json.JsonHash

open class DocGenerator(val markdown: Markdown = Markdown(), val headerSplit: Int = 1, build: DocGenerator.() -> Unit = {}) {
    init {
        build()
    }

    fun add(line: String = "") {
        if (markdown.lines.isEmpty() || markdown.lines.last().isNotBlank() || line.isNotBlank())
            markdown.add(line)
    }

    operator fun plusAssign(line: String) {
        add(line)
    }

    operator fun String.unaryPlus() {
        add(this)
    }

    fun code(type: String = "", code: String) {
        add("```$type")
        add(code)
        add("```")
        add()
    }

    fun code(json: JsonHash) {
        code("json", json.pretty(true).toString())
    }

    fun startTable(vararg columns: String) {
        startTable(columns.toList())
    }

    fun startTable(columns: List<String>) {
        add(columns.joinToString("|", "|", "|"))
        add(columns.joinToString("|", "|", "|") { "---" })
    }

    fun row(vararg cells: Any?) {
        row(cells.toList())
    }

    fun row(cells: List<Any?>) {
        add(cells.joinToString("|", "|", "|") {
            when (it) {
                null -> "` - `"
                is Boolean -> if (it) "Да" else "Нет"
                else -> it.toString()
            }
        })
    }

    fun header(title: String, init: DocGenerator.() -> Unit) {
        add("#".repeat(headerSplit) + " $title")
        add()

        val generator = DocGenerator(markdown, headerSplit + 1)
        generator.init()
    }

    fun paragraph(init: DocGenerator.() -> Unit) {
        val generator = DocGenerator(markdown, headerSplit)
        generator.init()

        add()
    }

    inline fun <reified T : Enum<*>> enumValues(): String = enumValues(T::class.java.enumConstants)

    fun <T : Enum<*>> enumValues(values: Array<T>): String {
        return enumValues(values.toList())
    }

    fun <T : Enum<*>> enumValues(values: List<T>): String {
        return values.joinToString(", ", "(", ")") { "`$it`" }
    }

    inline fun <reified T : Enum<*>> enumMessages(noinline ttt: (T) -> String): String = enumMessages<T>(T::class.java.enumConstants.toList(), ttt)

    fun <T : Enum<*>> enumMessages(values: Array<T>, title: (T) -> String): String {
        return enumMessages(values.toList(), title)
    }

    fun <T : Enum<*>> enumMessages(values: List<T>, title: (T) -> String): String {
        return values.joinToString(", ", "(", ")") {
            if (title(it).isBlank())
                "`$it`"
            else
                "`$it` - ${title(it)}"
        }
    }

    override fun toString(): String = markdown.toString()
}
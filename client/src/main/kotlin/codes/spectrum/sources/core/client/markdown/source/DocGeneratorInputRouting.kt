package codes.spectrum.sources.core.client.markdown.source

import codes.spectrum.sources.core.client.markdown.DocGenerator
import codes.spectrum.sources.core.client.markdown.Markdown
import codes.spectrum.utils.json.JsonHash

class DocGeneratorInputRouting(markdown: Markdown = Markdown(), headerSplit: Int = 1, val query: JsonHash, val path: String = "", val title: String = "")
    : DocGenerator(markdown, headerSplit) {
    fun route(path: String, title: String = "", routing: DocGeneratorInputRouting.() -> Unit) {
        val newPath = listOf(this.path, path).filter { it.isNotEmpty() }.joinToString(".")
        val newTitle = listOf(this.title, title).filter { it.isNotEmpty() }.joinToString(". ")

        val routeGenerator = DocGeneratorInputRouting(markdown, headerSplit, query, newPath, newTitle)
        routeGenerator.routing()
    }

    fun field(path: String, title: String, type: String, required: Boolean = false) {
        route(path, title) {
            val example = query.navigate(this.path.replace("[]", "[0]"))
            row(this.path, this.title, type, example, required)
        }
    }
}

fun DocGenerator.inputRouting(query: JsonHash, routing: DocGeneratorInputRouting.() -> Unit) {
    startTable("Путь до поля", "Описание", "Тип значений", "Пример", "Обязательность")

    val routeGenerator = DocGeneratorInputRouting(markdown, headerSplit, JsonHash.parse(query.toString()))
    routeGenerator.routing()

    add()
}
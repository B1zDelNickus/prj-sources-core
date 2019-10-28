package codes.spectrum.sources.core.client.markdown.source

import codes.spectrum.sources.core.client.markdown.DocGenerator
import codes.spectrum.sources.core.client.markdown.Markdown
import codes.spectrum.utils.json.JsonHash

@Deprecated("Использует генерацию путем описания полей, лучше использовать DocGeneratorOutputByJsonScheme")
class DocGeneratorOutputRouting(markdown: Markdown = Markdown(), headerSplit: Int = 1, val response: JsonHash, val path: String = "", val title: String = "")
    : DocGenerator(markdown, headerSplit) {
    fun route(path: String, title: String = "", routing: DocGeneratorOutputRouting.() -> Unit) {
        val newPath = listOf(this.path, path).filter { it.isNotEmpty() }.joinToString(".")
        val newTitle = listOf(this.title, title).filter { it.isNotEmpty() }.joinToString(". ")

        val routeGenerator = DocGeneratorOutputRouting(markdown, headerSplit, response, newPath, newTitle)
        routeGenerator.routing()
    }

    fun field(path: String, title: String, type: String) {
        route(path, title) {
            val example = response.navigate(this.path.replace("[]", "[0]"))
            row(this.path, this.title, type, example)
        }
    }
}

fun DocGenerator.outputRouting(response: JsonHash, routing: DocGeneratorOutputRouting.() -> Unit) {
    startTable("Путь до поля", "Описание", "Тип значений", "Пример")

    val routeGenerator = DocGeneratorOutputRouting(markdown, headerSplit, JsonHash.parse(response.toString()))
    routeGenerator.routing()

    add()
}
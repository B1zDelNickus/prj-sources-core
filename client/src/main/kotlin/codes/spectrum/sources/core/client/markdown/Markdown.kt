package codes.spectrum.sources.core.client.markdown

data class Markdown(val lines: MutableList<String> = mutableListOf()) {
    fun add(line: String = "") {
        lines += line
    }

    override fun toString(): String {
        return lines.joinToString("\n")
    }
}
package codes.spectrum.sources.core.source

import codes.spectrum.sources.core.source.StructureConstants.YAML_ARRAY_ELEMENT_INDENT
import codes.spectrum.sources.core.source.StructureConstants.YAML_INDENT

data class StructureParam(
    val name: String = "",
    val value: String? = null,
    val needQuotes: Boolean = true
) {
    fun toJson(): String =
        "\"${name}\":${ 
            if (value == null)
                "null"
            else {
                if (needQuotes)
                    "\"${value
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\"", "\\\"")
                    }\""
                else
                    value 
            } }"

    fun toYaml(): String =
        "${name}: ${ 
            if (value == null) "null" else { 
                if (value.contains("\n"))
                    "|-\n${value.lines().joinToString("\n") { "${YAML_INDENT.subSequence(0, YAML_INDENT.length - YAML_ARRAY_ELEMENT_INDENT.length)}$it" }}"
                else {
                    if (value.contains(": ") || value.contains("\"") || value.contains("**")) 
                        "\"${value.replace("\"", "\\\"")}\"" 
                    else 
                        value 
                } 
            }
        }"
}
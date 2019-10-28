package codes.spectrum.sources.core.source

import codes.spectrum.sources.core.source.StructureConstants.JSON_INDENT
import codes.spectrum.sources.core.source.StructureConstants.YAML_ARRAY_ELEMENT_INDENT
import codes.spectrum.sources.core.source.StructureConstants.YAML_INDENT

data class StructureObject (
    val params: List<StructureParam> = emptyList(),
    val objects: List<Pair<String, StructureObject>> = emptyList(),
    val arrays: List<Pair<String, List<StructureObject>>> = emptyList()
) {
    fun toJson(): String =
        "{\n" +
            (params.map { "$JSON_INDENT${it.toJson()}" } +
                objects.map { "$JSON_INDENT\"${it.first}\":\n${it.second.toJson().lines().joinToString("\n") { "$JSON_INDENT$it" }}" } +
                arrays.map { "$JSON_INDENT\"${it.first}\":[\n${it.second.joinToString(",\n") { it.toJson().lines().joinToString("\n") { "${JSON_INDENT.repeat(2)}$it" } }}\n$JSON_INDENT]" })
                .joinToString(",\n") +
            "\n}"

    fun toYaml(): String =
        (params.map { it.toYaml() } +
            objects.map { "${it.first}:\n${it.second.toYaml().lines().joinToString("\n") { "${YAML_INDENT.subSequence(0, YAML_INDENT.length - YAML_ARRAY_ELEMENT_INDENT.length)}$it" }}" } +
            arrays.map { "${it.first}:\n${it.second.joinToString("\n") { it.toYaml().lines().joinToString("\n") { "$YAML_INDENT$it" }.replace("^$YAML_INDENT".toRegex(), "${YAML_INDENT.subSequence(0, YAML_INDENT.length - YAML_ARRAY_ELEMENT_INDENT.length)}$YAML_ARRAY_ELEMENT_INDENT") }}" })
            .joinToString("\n")
}
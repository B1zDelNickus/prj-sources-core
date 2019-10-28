package codes.spectrum.sources.core.client.markdown.schemaDesc

data class EnumDescription(
        val className: String,
        val values: Map<String, String> = mapOf(),
        val example: String
) : IClassDescription
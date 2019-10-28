package codes.spectrum.sources.core.client.markdown.schemaDesc

data class FieldDescription(
        val field: String,
        val type: String,
        val description: String,
        val values: List<String>? = null,
        val ref: String? = null,
        val example: String? = null,
        val format: String? = null
)
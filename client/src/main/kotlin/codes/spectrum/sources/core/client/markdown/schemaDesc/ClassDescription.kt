package codes.spectrum.sources.core.client.markdown.schemaDesc

data class ClassDescription(
        val clazz: String,
        val description: String? = null,
        val fields: List<FieldDescription>
) : IClassDescription
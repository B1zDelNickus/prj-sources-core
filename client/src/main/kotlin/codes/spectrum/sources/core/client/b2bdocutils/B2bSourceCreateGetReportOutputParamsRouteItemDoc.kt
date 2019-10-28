package codes.spectrum.sources.core.client.b2bdocutils

data class B2bSourceCreateGetReportOutputParamsRouteItemDoc(
        var name: String = "",
        var description: String = "",
        var output_params: MutableList<B2bSourceCreateGetReportOutputParamsItemDoc> = mutableListOf()
) {

    fun items(): List<B2bSourceCreateGetReportOutputParamsItemDoc> = output_params.map {
        it.name = listOf(name, it.name).joinToString(".")
        it.description = listOf(description, it.description).filter { it.isNotBlank() }.joinToString(". ")
        it
    }

}

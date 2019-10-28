package codes.spectrum.sources.core.client.b2bdocutils

data class GetReportOutputParamsHeader(
        var name: String = "Название",
        var values: String = "Допустимые значения",
        var description: String = "Описание",
        var example: String = "Пример"
) {
    val isNotEmpty get() = name.isNotBlank() &&
            values.isNotBlank() &&
            description.isNotBlank() &&
            example.isNotBlank()
}
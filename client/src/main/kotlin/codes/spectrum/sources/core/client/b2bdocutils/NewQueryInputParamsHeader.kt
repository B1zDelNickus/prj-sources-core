package codes.spectrum.sources.core.client.b2bdocutils

data class NewQueryInputParamsHeader(
        var name: String = "Название",
        var required: String = "Обязательность",
        var values: String = "Допустимые значения",
        var description: String = "Описание",
        var example: String = "Пример"
) {
    val isNotEmpty get() = name.isNotBlank() &&
            required.isNotBlank() &&
            values.isNotBlank() &&
            description.isNotBlank() &&
            example.isNotBlank()
}

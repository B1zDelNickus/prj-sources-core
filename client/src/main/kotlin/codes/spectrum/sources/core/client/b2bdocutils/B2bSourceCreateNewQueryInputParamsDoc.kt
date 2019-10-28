package codes.spectrum.sources.core.client.b2bdocutils

data class B2bSourceCreateNewQueryInputParamsDoc(
        var header: NewQueryInputParamsHeader = NewQueryInputParamsHeader(),
        var input_params: MutableList<B2bSourceCreateNewQueryInputParamsItemDoc> = defaultInputParams
) {
    companion object {
        val defaultInputParams= mutableListOf(
                B2bSourceCreateNewQueryInputParamsItemDoc(
                        "Authorization",
                        true,
                        "String",
                        "Токен авторизации",
                        "AR-REST Ge4tffdsg4gdfv6gf10"
                ),
                B2bSourceCreateNewQueryInputParamsItemDoc(
                        "REPORT_TYPE_UID",
                        true,
                        "String",
                        "Уникальный идентификатор типа отчета",
                        "report_type_name@domain_name"
                ),
                B2bSourceCreateNewQueryInputParamsItemDoc(
                        "queryType",
                        true,
                        "String",
                        "Тип запроса",
                        "MULTIPART"
                ),
                B2bSourceCreateNewQueryInputParamsItemDoc(
                        "query",
                        true,
                        "String",
                        "Значение простого запроса",
                        "пустое значение"
                )
        )
    }

    val isNotEmpty get() = this.header.isNotEmpty && input_params.size != 0
}
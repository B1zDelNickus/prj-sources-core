package codes.spectrum.sources.core.client.b2bdocutils

data class B2bSourceCreateGetReportInputParamsDoc(
        var header: GetReportInputParamsHeader = GetReportInputParamsHeader(),
        var input_params: MutableList<B2bSourceCreateGetReportInputParamsItemDoc> = defaultInputParams
) {
    companion object {
        val defaultInputParams= mutableListOf(
                B2bSourceCreateGetReportInputParamsItemDoc(
                        "Authorization",
                        true,
                        "String",
                        "Токен авторизации",
                        "AR-REST Ge4tffdsg4gdfv6gf10"
                ),
                B2bSourceCreateGetReportInputParamsItemDoc(
                        "REPORT_DESC",
                        true,
                        "String",
                        "Уникальный идентификатор отчета",
                        "some_report_uid"
                ),
                B2bSourceCreateGetReportInputParamsItemDoc(
                        "_content",
                        true,
                        "Boolean",
                        "Показать контент отчета",
                        "true"
                )
        )
    }

    val isNotEmpty get() = this.header.isNotEmpty && input_params.size != 0
}
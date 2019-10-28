package codes.spectrum.sources.core.client.b2bdocutils

data class B2bSourceCreateGetReportOutputParamsDoc(
        var header: GetReportOutputParamsHeader = GetReportOutputParamsHeader(),
        var output_params: MutableList<B2bSourceCreateGetReportOutputParamsItemDoc> = defaultOutputParams
) {
    companion object {
        val defaultOutputParams = mutableListOf(
                B2bSourceCreateGetReportOutputParamsItemDoc("state", "String", "Статус обработки запроса", "ok"),
                B2bSourceCreateGetReportOutputParamsItemDoc("size", "Number (Integer)", "Кол-во элементов в массиве data", "1"),
                B2bSourceCreateGetReportOutputParamsItemDoc("stamp", "String", "Дата создания запроса", "2018-11-20T11:06:37.785Z"),
                B2bSourceCreateGetReportOutputParamsItemDoc("data[].domain_uid", "String", "Уникальный идентификатор домена", "some_domain_uid"),
                B2bSourceCreateGetReportOutputParamsItemDoc("data[].report_type_uid", "String", "Уникальный идентификатор типа отчета", "some_report_type_uid"),
                B2bSourceCreateGetReportOutputParamsItemDoc("data[].progress_ok", "Number (Integer)", "Кол-во источников в выполненном состоянии", "4"),
                B2bSourceCreateGetReportOutputParamsItemDoc("data[].progress_wait", "Number (Integer)", "Кол-во источников в процессе выполнения", "2"),
                B2bSourceCreateGetReportOutputParamsItemDoc("data[].progress_error", "Number (Integer)", "Кол-во источников завершившихся ошибкой", "0"),
                B2bSourceCreateGetReportOutputParamsItemDoc("data[].state.sources[]._id", "String", "Название источника", "some_source_name"),
                B2bSourceCreateGetReportOutputParamsItemDoc("data[].state.sources[].state", "String (OK, ERROR, PROGRESS, SKIP)", "Состояние источника", "OK"),
                B2bSourceCreateGetReportOutputParamsItemDoc("data[].query.type", "String", "Тип запроса", "MULTIPART")
        )
    }

    val isNotEmpty get() = this.header.isNotEmpty && output_params.size != 0
}
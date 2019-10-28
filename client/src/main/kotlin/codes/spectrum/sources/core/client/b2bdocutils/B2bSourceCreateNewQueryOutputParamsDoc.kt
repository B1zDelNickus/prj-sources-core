package codes.spectrum.sources.core.client.b2bdocutils

data class B2bSourceCreateNewQueryOutputParamsDoc(
        var header: NewQueryOutputParamsHeader = NewQueryOutputParamsHeader(),
        var output_params: MutableList<B2bSourceCreateNewQueryOutputParamsItemDoc> = defaultOutputParams
) {
    companion object {
        val defaultOutputParams = mutableListOf(
                B2bSourceCreateNewQueryOutputParamsItemDoc("state", "String", "Состояние работы источника", "ok"),
                B2bSourceCreateNewQueryOutputParamsItemDoc("size", "Number (Integer)", "Кол-во элементов в массиве data", "1"),
                B2bSourceCreateNewQueryOutputParamsItemDoc("stamp", "String", "Дата создания запроса", "2018-11-20T11:06:37.785Z"),
                B2bSourceCreateNewQueryOutputParamsItemDoc("data[].uid", "String", "Уникальный идентификатор отчета", "some_report_uid"),
                B2bSourceCreateNewQueryOutputParamsItemDoc("data[].isnew", "Boolean", "Признак создания нового отчета", "true")
        )
    }

    val isNotEmpty get() = this.header.isNotEmpty && output_params.size != 0
}
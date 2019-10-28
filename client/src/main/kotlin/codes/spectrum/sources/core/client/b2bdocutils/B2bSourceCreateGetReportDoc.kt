package codes.spectrum.sources.core.client.b2bdocutils

data class B2bSourceCreateGetReportDoc(
        var url: String = """https://b2b-api.checkperson.ru/b2b/api/v1/user/reports/{REPORT_DESC}?_content=true""",
        var destination: String = "Получение отчета",
        var type: String = "GET",
        var encoding: String = "UTF-8",
        var input: B2bSourceCreateGetReportInputParamsDoc = B2bSourceCreateGetReportInputParamsDoc(),
        var output: B2bSourceCreateGetReportOutputParamsDoc = B2bSourceCreateGetReportOutputParamsDoc(),
        var query_example: GetQueryExample = GetQueryExample(),
        var response_example: GetResponseExample = GetResponseExample()
){
    infix fun url(_url: String){
        this.url = _url
    }

    infix fun destination(_destination: String){
        this.destination = _destination
    }

    infix fun type(_type: String){
        this.type = _type
    }

    infix fun encoding(_encoding: String){
        this.encoding = _encoding
    }

    val isNotEmpty get() = this.url.isNotBlank() &&
            this.destination.isNotBlank() &&
            this.type.isNotBlank() &&
            this.encoding.isNotBlank() &&
            this.input.isNotEmpty &&
            this.output.isNotEmpty
}

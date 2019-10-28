package codes.spectrum.sources.core.client.b2bdocutils

data class B2bSourceCreateNewQueryDoc(
        var url: String = """https://b2b-api.checkperson.ru/b2b/api/v1/user/reports/{REPORT_TYPE_UID}/_make""",
        var destination: String = "Создание нового запроса",
        var type: String = "POST",
        var encoding: String = "UTF-8",
        var input: B2bSourceCreateNewQueryInputParamsDoc = B2bSourceCreateNewQueryInputParamsDoc(),
        var output: B2bSourceCreateNewQueryOutputParamsDoc = B2bSourceCreateNewQueryOutputParamsDoc(),
        var query_example: QueryExample = QueryExample(),
        var response_example: ResponseExample = ResponseExample(),
        var response_fail: ResponseFailExample = ResponseFailExample()
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
            this.output.isNotEmpty &&
            this.query_example.isNotEmpty &&
            this.response_example.isNotEmpty &&
            this.response_fail.isNotEmpty
}
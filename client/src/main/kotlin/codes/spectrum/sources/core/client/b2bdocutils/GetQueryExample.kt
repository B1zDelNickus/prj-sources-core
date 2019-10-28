package codes.spectrum.sources.core.client.b2bdocutils

data class GetQueryExample(
        var url: String = """https://b2b-api.checkperson.ru/b2b/api/v1/user/reports/{REPORT_UID}?_content=true""",
        var method: String = "GET",
        var header: String = "Authorization = AR-REST Ge4tffdsg4gdfv6gf10"
) {

    infix fun url(_url: String){
        this.url = _url
    }

    infix fun method(_method: String){
        this.method = _method
    }

    infix fun header(_header: String){
        this.header = _header
    }
}
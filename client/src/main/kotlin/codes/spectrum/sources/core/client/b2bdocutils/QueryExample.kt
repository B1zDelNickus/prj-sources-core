package codes.spectrum.sources.core.client.b2bdocutils

data class QueryExample(
        var url: String = """https://b2b-api.checkperson.ru/b2b/api/v1/user/reports/{REPORT_TYPE_UID}/_make""",
        var method: String = "POST",
        var header: String = "Authorization = AR-REST Ge4tffdsg4gdfv6gf10",
        var body: Any? = null
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

    val isNotEmpty get() = url.isNotBlank() && method.isNotBlank() && header.isNotBlank() && body != null
}
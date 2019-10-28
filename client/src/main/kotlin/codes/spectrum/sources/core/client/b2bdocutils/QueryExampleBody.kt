package codes.spectrum.sources.core.client.b2bdocutils

data class QueryExampleBody(
        var queryType: String = "MULTIPART",
        var query: String = " ",
        var data: Any? = null
) {

    infix fun `query type`(_queryType: String){
        this.queryType = _queryType
    }

    infix fun query(_query: String){
        this.query = query
    }

    infix fun data(_data: Any?){
        this.data = _data
    }
}
package codes.spectrum.sources.core.client.b2bdocutils

import codes.spectrum.utils.json.JsonHash

fun B2bSourceCreateGetReportDoc.`query example`(block: GetQueryExample.() -> Unit){
    this.query_example = GetQueryExample().apply(block)
}

fun B2bSourceCreateGetReportDoc.`response example`(block: GetResponseExample.() -> Unit){
    this.response_example = GetResponseExample().apply(block)
}

fun GetResponseExampleData.query(block: GetResponseExampleDataQuery.() -> Unit){
    this.query = GetResponseExampleDataQuery().apply(block)
}

fun GetResponseExampleDataStateSource.data(block: JsonHash.() -> Unit){
    this.data = JsonHash().apply(block)
}

fun GetResponseExampleDataState.data(block: JsonHash.() -> Unit){
    this.data = JsonHash().apply(block)
}

fun GetResponseExampleData.state(block: GetResponseExampleDataState.() -> Unit){
    this.state = GetResponseExampleDataState().apply(block)
}

fun GetResponseExampleDataContent.`check person`(block: Any.() -> Unit){
    this.check_person = Any().apply(block)
}



data class GetResponseExampleDataContentTest(
        var code: String =  "1.1",
        var title: String =  "Найдены данные, относящиеся к заданному перечню статей УК"
)



fun GetResponseExample.item(block: GetResponseExampleData.() -> Unit){
    this.data.add(GetResponseExampleData().apply(block))
}

fun b2bDoc(block: B2bSourceDoc.() -> Unit): B2bSourceDoc = B2bSourceDoc().apply(block)

fun B2bSourceDoc.header(block: B2bSourceHeaderDoc.() -> Unit) {
    header = B2bSourceHeaderDoc().apply(block)
}

fun B2bSourceDoc.`new query`(block: B2bSourceCreateNewQueryDoc.() -> Unit){
    new_query = B2bSourceCreateNewQueryDoc().apply(block)
}

fun B2bSourceDoc.`get report`(block: B2bSourceCreateGetReportDoc.() -> Unit){
    get_report = B2bSourceCreateGetReportDoc().apply(block)
}

fun B2bSourceCreateNewQueryInputParamsDoc.item(block: B2bSourceCreateNewQueryInputParamsItemDoc.() -> Unit){
    this.input_params.add(B2bSourceCreateNewQueryInputParamsItemDoc().apply(block))
}

fun B2bSourceCreateNewQueryDoc.input(block: B2bSourceCreateNewQueryInputParamsDoc.() -> Unit){
    this.input = B2bSourceCreateNewQueryInputParamsDoc().apply(block)
}

fun B2bSourceCreateGetReportOutputParamsDoc.item(block: B2bSourceCreateGetReportOutputParamsItemDoc.() -> Unit){
    this.output_params.add(B2bSourceCreateGetReportOutputParamsItemDoc().apply(block))
}

fun B2bSourceCreateGetReportOutputParamsDoc.route(path: String, description: String = "", block: B2bSourceCreateGetReportOutputParamsRouteItemDoc.() -> Unit){
    val route = B2bSourceCreateGetReportOutputParamsRouteItemDoc(path, description).apply(block)
    this.output_params.addAll(route.items())
}

fun B2bSourceCreateGetReportOutputParamsRouteItemDoc.route(path: String, description: String = "", block: B2bSourceCreateGetReportOutputParamsRouteItemDoc.() -> Unit){
    val route = B2bSourceCreateGetReportOutputParamsRouteItemDoc(path, description).apply(block)
    this.output_params.addAll(route.items())
}

fun B2bSourceCreateGetReportOutputParamsRouteItemDoc.item(block: B2bSourceCreateGetReportOutputParamsItemDoc.() -> Unit){
    val item = B2bSourceCreateGetReportOutputParamsItemDoc().apply(block)
    this.output_params.add(item)
}

fun B2bSourceCreateGetReportDoc.output(block: B2bSourceCreateGetReportOutputParamsDoc.() -> Unit){
    this.output = B2bSourceCreateGetReportOutputParamsDoc().apply(block)
}

fun B2bSourceCreateNewQueryDoc.`query example`(block: QueryExample.() -> Unit){
    this.query_example = QueryExample().apply(block)
}

fun B2bSourceCreateNewQueryDoc.`response example`(block: ResponseExample.() -> Unit){
    this.response_example = ResponseExample().apply(block)
}

fun B2bSourceCreateNewQueryDoc.`response fail`(block: ResponseFailExample.() -> Unit){
    this.response_fail = ResponseFailExample().apply(block)
}

fun QueryExample.body(body: QueryExampleBody.() -> Unit){
    this.body = QueryExampleBody().apply(body)
}

fun ResponseExample.body(body: ResponseExampleBody.() -> Unit){
    this.body = ResponseExampleBody().apply(body)
}

fun ResponseExampleBody.data(body: ResponseExampleBodyOk.() -> Unit){
    this.data.add(ResponseExampleBodyOk().apply(body))
}

fun ResponseFailExample.event(body: ResponseFailEvent.() -> Unit){
    this.event = ResponseFailEvent().apply(body)
}

fun ResponseFailEvent.data(body: ResponseFailEventData.() -> Unit){
    this.data = ResponseFailEventData().apply(body)
}
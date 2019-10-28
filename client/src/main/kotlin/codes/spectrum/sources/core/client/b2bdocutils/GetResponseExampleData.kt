package codes.spectrum.sources.core.client.b2bdocutils

data class GetResponseExampleData(
        var domain_uid: String = "some_domain_uid",
        var report_type_uid: String = "some_report_type_uid@some_domain_uid",
        var query: GetResponseExampleDataQuery = GetResponseExampleDataQuery(),
        var progress_ok: Int = 1,
        var progress_wait: Int = 0,
        var progress_error: Int = 0,
        var state: GetResponseExampleDataState = GetResponseExampleDataState(),
        var content: Any = GetResponseExampleDataContent()
){

    infix fun content(_content: Any){
        this.content = _content
    }

    infix fun `domain uid`(_domain_uid: String){
        this.domain_uid = _domain_uid
    }

    infix fun `report type uid`(_report_type_uid: String){
        this.report_type_uid = _report_type_uid
    }

    infix fun `progress ok`(_progress_ok: Int){
        this.progress_ok = _progress_ok
    }

    infix fun `progress wait`(_progress_wait: Int){
        this.progress_wait = progress_wait
    }

    infix fun `progress error`(_progress_error: Int){
        this.progress_error = _progress_error
    }
}
package codes.spectrum.sources.core.client.b2bdocutils

data class ResponseExampleBodyOk(
        var uid: String = "some_report_uid@some_domain_uid",
        var isnew: Boolean =  true,
        var process_request_uid: String = "some_process_request_uid",
        var suggest_get: String = "2018-11-20T11:06:37.569Z"
) {

    infix fun uid(_uid: String){
        this.uid = _uid
    }

    infix fun isnew(_isnew: Boolean){
        this.isnew = _isnew
    }

    infix fun `process request uid`(_process_request_uid: String){
        this.process_request_uid = _process_request_uid
    }

    infix fun `suggest get`(_suggest_get: String){
        this.suggest_get = _suggest_get
    }
}
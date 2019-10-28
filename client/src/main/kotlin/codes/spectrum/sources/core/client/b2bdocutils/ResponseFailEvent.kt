package codes.spectrum.sources.core.client.b2bdocutils

data class ResponseFailEvent(
        var uid: String = "",
        var stamp: String = "2018-11-20T12:43:17.062Z",
        var cls: String = "Data",
        var type: String = "DataSeekObjectError",
        var name: String = "Отсутствие объекта с заданным идентификатором",
        var message: String = "Отсутствует объект типа api.model.Report_Type с UID some_report_type_uid@some_domain",
        var data: ResponseFailEventData = ResponseFailEventData(),
        var events: MutableList<String> = mutableListOf()
){

    infix fun uid(_uid: String){
        this.uid = _uid
    }

    infix fun stamp(_stamp: String){
        this.stamp = _stamp
    }

    infix fun cls(_cls: String){
        this.cls = _cls
    }

    infix fun type(_type: String){
        this.type = _type
    }

    infix fun name(_name: String){
        this.name = _name
    }

    infix fun message(_message: String){
        this.message = _message
    }

    val isNotEmpty get() = true
}
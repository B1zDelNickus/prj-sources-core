package codes.spectrum.sources.core.client.b2bdocutils

data class ResponseFailExample(
        var state: String = "fail",
        var stamp: String = "2018-11-20T12:43:17.065Z",
        var event: ResponseFailEvent = ResponseFailEvent()
) {

    infix fun state(_state: String){
        this.state = _state
    }

    infix fun stamp(_stamp: String){
        this.stamp = _stamp
    }

    val isNotEmpty get() = state.isNotBlank() &&
            stamp.isNotBlank() &&
            event.isNotEmpty
}
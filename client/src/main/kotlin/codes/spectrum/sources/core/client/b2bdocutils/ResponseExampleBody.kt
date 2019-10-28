package codes.spectrum.sources.core.client.b2bdocutils

data class ResponseExampleBody(
        var state: String = "ok",
        var size: Int = 1,
        var stamp: String = "2018-11-20T11:06:37.785Z",
        var data: MutableList<Any> = mutableListOf()
) {

    infix fun state(_state: String){
        this.state = _state
    }

    infix fun size(_size: Int){
        this.size = _size
    }

    infix fun stamp(_stamp: String){
        this.stamp = _stamp
    }
}
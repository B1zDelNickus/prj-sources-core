package codes.spectrum.sources.core.client.b2bdocutils

data class GetResponseExample(
        var state: String = "ok",
        var size: Int = 1,
        var stamp: String = "2018-11-20T13:59:27.141Z",
        var data: MutableList<GetResponseExampleData> = mutableListOf()
) {

    infix fun state(_state: String){
        this.state = _state
    }

    infix fun size(_size: Int){
        this.size = _size
    }

    infix fun stamp(_stamp: String){
        this.state = _stamp
    }
}
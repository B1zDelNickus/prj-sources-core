package codes.spectrum.sources.core.client.b2bdocutils

import codes.spectrum.utils.json.JsonHash

data class GetResponseExampleDataStateSource(
        var _id: String = "some_source",
        var state: String = "OK",
        var data: JsonHash = JsonHash()
) {

    infix fun id(_id: String){
        this._id = _id
    }

    infix fun state(_state: String){
        this.state = _state
    }
}
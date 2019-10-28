package codes.spectrum.sources.core.client.b2bdocutils

import codes.spectrum.utils.json.JsonHash

data class GetResponseExampleDataQuery(
        var type: String = "MULTIPART",
        var body: String = " ",
        var data: JsonHash = JsonHash()
) {

    infix fun data(_data: JsonHash){
        this.data = _data
    }

    infix fun type(_type: String){
        this.type = _type
    }

    infix fun body(_body: String){
        this.body = _body
    }
}
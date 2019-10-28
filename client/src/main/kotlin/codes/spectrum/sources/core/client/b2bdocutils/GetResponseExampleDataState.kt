package codes.spectrum.sources.core.client.b2bdocutils

import codes.spectrum.utils.json.JsonHash

data class GetResponseExampleDataState(
        var sources: MutableList<GetResponseExampleDataStateSource> = default,
        var data: JsonHash = JsonHash()
){

    companion object {
        val default = mutableListOf(
                GetResponseExampleDataStateSource()
        )
    }
}
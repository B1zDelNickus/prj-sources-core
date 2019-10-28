package codes.spectrum.sources.core.client.b2bdocutils

data class B2bSourceCreateNewQueryOutputParamsItemDoc(
        var name: String = "",
        var values: String = "",
        var description: String = "",
        var example: String = ""
) {

    infix fun name(_name: String){
        this.name = _name
    }

    infix fun values(_values: String){
        this.values = _values
    }

    infix fun description(_description: String){
        this.description = _description
    }

    infix fun example(_example: String){
        this.example = _example
    }
}
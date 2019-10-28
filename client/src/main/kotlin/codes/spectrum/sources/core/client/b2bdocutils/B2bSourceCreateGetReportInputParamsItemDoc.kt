package codes.spectrum.sources.core.client.b2bdocutils

data class B2bSourceCreateGetReportInputParamsItemDoc(
        var name: String = "",
        var required: Boolean = false,
        var values: String = "",
        var description: String = "",
        var example: String = ""
) {

    infix fun name(_name: String){
        this.name = _name
    }

    infix fun required(_required: Boolean){
        this.required = _required
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
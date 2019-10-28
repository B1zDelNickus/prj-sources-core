package codes.spectrum.sources.core.client.b2bdocutils

data class ResponseFailEventData(
        var entity_type: String = "api.model.Report_Type",
        var entity_uid: String = "some_report_type_uid@some_domain"
) {

    infix fun `entity type`(_entity_type: String){
        this.entity_type = _entity_type
    }

    infix fun `entity uid`(_entity_uid: String){
        this.entity_uid = _entity_uid
    }
}
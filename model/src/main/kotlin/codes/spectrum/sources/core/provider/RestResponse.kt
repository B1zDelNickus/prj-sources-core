package codes.spectrum.sources.core.provider

import codes.spectrum.api.SourceState

interface IRestResponseSetup {
    fun setup(response: RestResponse)
}

class RestResponse {
    var uid:String? = null
    var status:SourceState = SourceState.NONE
    var request:Any? = null
    var data:Any? = null
    var error:Throwable? = null
    companion object {
        fun create(data:Any?) : RestResponse{
            val result = RestResponse()
            if(null==data){
                result.status = SourceState.NOT_FOUND
            }else{
                if(data is Throwable){
                    result.error = data
                    result.status = SourceState.GENERAL_ERROR
                }else if(data is SourceState){
                    result.status = data
                }else if(data is IRestResponseSetup){
                    data.setup(result)
                }else{
                    result.data = data
                    result.status = SourceState.OK
                }
            }
            return result
        }
    }
}
package codes.spectrum.sources.config

data class AnnotatedProperty (
    val value:Any? = null,
    val requestedName:String = "",
    val internalName:String = "",
    val evidence:String="",
    val defined:Boolean = false,
    val overriden:Boolean = false
){
    var sourceInstance: IConfig? = null
}
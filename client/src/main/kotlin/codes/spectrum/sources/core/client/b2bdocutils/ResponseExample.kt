package codes.spectrum.sources.core.client.b2bdocutils

data class ResponseExample(
        var body: Any? = null
) {

    val isNotEmpty get() = body != null
}
val spectrumVer = property("spectrum-version") as String

dependencies{
    api("codes.spectrum:spectrum-core:$spectrumVer")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0")
    apiCore(config = "implementation", moduleName = "core")
}
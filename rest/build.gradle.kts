setupKtor()
val spectrumVer = property("spectrum-version") as String

dependencies{
    apiCore(config = "implementation", moduleName = "core")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
}
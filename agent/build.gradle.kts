val spectrum_legacy_version = property("spectrum-version") as String

setupKtor()
dependencies{
    implementation( project (":transport"))
    api(project(":rest"))
    api(project(":bus"))
}

val spectrum_legacy_version = property("spectrum-version") as String

dependencies {
    implementation("de.siegmar:fastcsv:1.0.3")
    implementation("codes.spectrum:spectrum-utils:$spectrum_legacy_version")
    implementation("codes.spectrum:spectrum-hadoop:$spectrum_legacy_version")
}
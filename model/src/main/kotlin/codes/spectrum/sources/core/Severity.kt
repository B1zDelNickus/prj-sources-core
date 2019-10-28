package codes.spectrum.sources.core

@Deprecated("use codes.spectrum.api.Severity", level = DeprecationLevel.WARNING, replaceWith = ReplaceWith("Severity", "codes.spectrum.api.Severity"))
enum class Severity(val level:Int) {
    OK(10),
    HINT(20),
    WARN(30),
    ERROR(40)
}
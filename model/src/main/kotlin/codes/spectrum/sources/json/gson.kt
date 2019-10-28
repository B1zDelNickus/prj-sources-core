package codes.spectrum.sources.json

import codes.spectrum.serialization.json.spectrumDefaults
import com.google.gson.GsonBuilder

@Deprecated(
    message = "Нужно использовать новое расширение для GSON",
    replaceWith = ReplaceWith("codes.spectrum.serialization.json.Json"),
    level = DeprecationLevel.ERROR)
val coreGson = GsonBuilder().spectrumDefaults().create()
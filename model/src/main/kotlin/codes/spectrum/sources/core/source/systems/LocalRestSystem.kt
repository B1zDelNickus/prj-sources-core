package codes.spectrum.sources.core.source.systems

import codes.spectrum.sources.core.source.RestSystem

object LocalRestSystem : RestSystem(
    name = "Локальный",
    url = "http://127.0.0.1:8080/",
    index = 3
)
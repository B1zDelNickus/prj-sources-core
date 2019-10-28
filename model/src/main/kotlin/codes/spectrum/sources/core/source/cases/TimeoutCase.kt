package codes.spectrum.sources.core.source.cases

import codes.spectrum.sources.DebugMode
import codes.spectrum.sources.core.source.Case

object TimeoutCase : Case(
    name = "Имитация таймаута",
    timeout = 10L,
    debug = DebugMode(sourceDelay = 100L),
    description = "Кейс имитации *таймаута*"
)
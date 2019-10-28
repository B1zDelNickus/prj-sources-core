package codes.spectrum.sources.core.source.cases

import codes.spectrum.sources.DebugMode
import codes.spectrum.sources.core.source.Case

object ErrorCase : Case(
    name = "Имитация ошибки",
    debug = DebugMode(throwError = true),
    description = "Кейс имитации *ошибки*"
)
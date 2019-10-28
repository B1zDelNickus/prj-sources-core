package codes.spectrum.sources

import codes.spectrum.api.SourceState

open class SourceResult <TResult>(
    var status: SourceState = SourceState.NONE,
    var data:TResult,
    var error : Throwable? = null
)
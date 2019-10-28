package codes.spectrum.sources.core.client.b2bdocutils

import java.lang.StringBuilder

data class B2bSourceDocContext(
        var doc: B2bSourceDoc = B2bSourceDoc(),
        var builder: StringBuilder = StringBuilder(),
        var state: B2bDocState = B2bDocState.NONE,
        var result: String = ""
)
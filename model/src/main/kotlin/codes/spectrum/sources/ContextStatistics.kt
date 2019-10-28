package codes.spectrum.sources

import java.util.concurrent.atomic.AtomicInteger


data class ContextStatistics(
    var total: AtomicInteger = AtomicInteger(0),
    var successful: AtomicInteger = AtomicInteger(0),
    var error: AtomicInteger = AtomicInteger(0)
)
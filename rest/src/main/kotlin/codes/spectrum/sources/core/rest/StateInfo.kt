package codes.spectrum.sources.core.rest

import java.math.BigDecimal
import java.math.RoundingMode
import java.net.InetAddress
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

data class StateSnapShot(
    var time: Date = Date(),
    var hostName: String = "",
    var os: String = "",
    var startTime: Date = Date(),
    var upTime: String = "",
    val calls: Calls = Calls(),
    val errors: Calls = Calls(),
    val memory: Memory = Memory()

) {
    data class Memory(
        var total: BigDecimal = BigDecimal.ZERO,
        var free: BigDecimal = BigDecimal.ZERO,
        var used: BigDecimal = BigDecimal.ZERO
    ) {
        fun setup() {
            used = total - free
        }
    }

    data class Calls(
        var total: Int = 0,
        var source: Int = 0,
        var admin: Int = 0,
        var ui: Int = 0,
        var doc: Int = 0
    ) {
        fun setup() {
            total = admin + ui + doc + source
        }
    }

    fun setup() {
        memory.setup()
        calls.setup()
        errors.setup()
        upTime = Duration.ofMillis(time.time - startTime.time).toString()
    }
}

class StateInfo {
    val adminCalls = AtomicInteger()
    val uiCalls = AtomicInteger()
    val docCalls = AtomicInteger()
    val sourceCalls = AtomicInteger()
    val adminErrors = AtomicInteger()
    val uiErrors = AtomicInteger()
    val docErrors = AtomicInteger()
    val sourceErrors = AtomicInteger()
    val startTime = Date()
    fun get(): StateSnapShot {
        val runtime = Runtime.getRuntime()
        val ip = InetAddress.getLocalHost();
        return StateSnapShot().apply {
            hostName = ip.getHostName()
            os = System.getProperty("os.name").toLowerCase();
            startTime = this@StateInfo.startTime
            memory.apply {
                total = BigDecimal.valueOf(runtime.totalMemory().toDouble() / (1024 * 1024)).setScale(2, RoundingMode.CEILING)
                free = BigDecimal.valueOf(runtime.freeMemory().toDouble() / (1024 * 1024)).setScale(2, RoundingMode.CEILING)
            }
            calls.apply {
                admin = this@StateInfo.adminCalls.get()
                ui = this@StateInfo.uiCalls.get()
                doc = this@StateInfo.docCalls.get()
                source = this@StateInfo.sourceCalls.get()
            }
            errors.apply {
                admin = this@StateInfo.adminErrors.get()
                ui = this@StateInfo.uiErrors.get()
                doc = this@StateInfo.docErrors.get()
                source = this@StateInfo.sourceErrors.get()
            }
            setup()
        }
    }

    companion object {
        val Instance = StateInfo()
    }
}
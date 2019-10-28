package codes.spectrum.sources.core.service

import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty


fun runServer(
    reloadRoot: String = ".",
    connectionGroupSize: Int = 5,
    workerGroupSize: Int = 5,
    callGroupSize: Int = 8,
    module: Application.() -> Unit
) {
    embeddedServer(Netty
        , (System.getenv("PORT") ?: "8080").toInt()
        , module = module
        , configure = {
            this.connectionGroupSize = connectionGroupSize
            this.workerGroupSize = workerGroupSize
            this.callGroupSize = callGroupSize
        }
    ).start(wait = true)
}
package codes.spectrum.sources.core.rest

import codes.spectrum.serialization.json.spectrumDefaults
import codes.spectrum.api.Severity
import codes.spectrum.sources.core.SourceDefinition
import codes.spectrum.api.SourceState
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.util.pipeline.PipelineContext
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.File
import java.util.*


fun Application.defaultRouting(sourceCall: suspend PipelineContext<Unit, ApplicationCall>.() -> SourceState) {
    routing {
        route("/") {
            get {
                renderUI()
            }

            get("doc/{name?}") {
                renderDoc()
            }

            get("state") {
                getState()
            }

            get("insomnia.json") {
                getFile("insomnia.json")
            }

            get("insomnia.yaml") {
                getFile("insomnia.yaml")
            }
        }
        route(SourceDefinition.Instance.serviceApiRoot) {
            get("ui") {
                renderUI()
            }

            get("state") {
                getState()
            }

            get("doc/{name?}") {
                renderDoc()
            }

            get("insomnia.json") {
                getFile("insomnia.json")
            }

            get("insomnia.yaml") {
                getFile("insomnia.yaml")
            }

            route("execute") {
                handle {
                    StateInfo.Instance.sourceCalls.incrementAndGet()
                    try {
                        val status = sourceCall()
                        if (status.severity == Severity.ERROR) {
                            StateInfo.Instance.sourceErrors.incrementAndGet()
                        }
                    } catch (e: Exception) {
                        StateInfo.Instance.sourceErrors.incrementAndGet()
                        throw e
                    }
                }
            }
        }
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.runWithStateInfo(sourceCall: suspend PipelineContext<Unit, ApplicationCall>.() -> SourceState) {
    StateInfo.Instance.sourceCalls.incrementAndGet()
    try {
        val status = sourceCall()
        if (status.severity == Severity.ERROR) {
            StateInfo.Instance.sourceErrors.incrementAndGet()
        }
    } catch (e: Throwable) {
        StateInfo.Instance.sourceErrors.incrementAndGet()
        throw e
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.getState() {
    StateInfo.Instance.adminCalls.incrementAndGet()
    try {
        this.call.respond(StateInfo.Instance.get())
    } catch (e: Throwable) {
        StateInfo.Instance.adminErrors.incrementAndGet()
        this.call.respond(HttpStatusCode.InternalServerError, object {})
    }
}

private val extensions = Arrays.asList(TablesExtension.create())
private val parser = Parser.builder().extensions(extensions).build()
private val renderer = HtmlRenderer.builder().extensions(extensions).build()
suspend fun PipelineContext<Unit, ApplicationCall>.renderDoc() {
    StateInfo.Instance.docCalls.incrementAndGet()
    try {
        var name = this.context.parameters["name"] ?: "README.md"
        if (name.isEmpty()) {
            name = "README.md"
        }
        if (!name.endsWith(".md")) {
            name += ".md"
        }
        val resourceName = "doc/${name}"
        val template = when {
            File("./src/main/resources/doctemplate.html").exists() -> File("./src/main/resources/doctemplate.html").readText()
            else -> ClassLoader.getSystemResourceAsStream("doctemplate.html").reader().readText()
        }
        try {
            val markdown = when {
                File("./${name}").exists() -> File("./${name}").readText()
                File("../${name}").exists() -> File("../${name}").readText()
                else -> ClassLoader.getSystemResourceAsStream(resourceName).reader().readText()
            }

            val document = parser.parse(markdown)
            var html = renderer.render(document)
            html = template.replace("~{DOC_NAME}", name).replace("~{DOC_CONTENT}", html)
            this.context.respondText(html, ContentType.Text.Html)
        } catch (e: Exception) {
            StateInfo.Instance.docErrors.incrementAndGet()
            this.context.respondText("File Not found ${name} (${e.message})", ContentType.Text.Plain, HttpStatusCode.NotFound)
        }
    } catch (e: Throwable) {
        StateInfo.Instance.docErrors.incrementAndGet()
        this.context.respondText("Some error with doc: ${e.javaClass.name} - ${e.message}", ContentType.Text.Plain, HttpStatusCode.NotFound)
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.renderUI() {
    StateInfo.Instance.uiCalls.incrementAndGet()
    val localFile = File("./src/main/resources/ui.html")
    if (localFile.exists()) {
        context.respondText(localFile.readText(), ContentType.Text.Html)
    } else {
        context.respondText(ClassLoader.getSystemResourceAsStream("ui.html").reader().readText(), ContentType.Text.Html)
    }
}


fun Application.setupNegotiation() {
    install(ContentNegotiation) {
        gson {
            spectrumDefaults()
            //TODO: setup custom gson for source `demo`
        }
    }
}

fun Application.setupCors() {
    install(CORS) {
        anyHost()
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.getFile(name: String = "") {
    StateInfo.Instance.uiCalls.incrementAndGet()
    val localFile: File = when {
        File("./$name").exists() -> File("./$name")
        File("../$name").exists() -> File("../$name")
        else -> File(ClassLoader.getSystemResource(name).toURI())
    }
    if (localFile.exists()) {
        context.response.header("Content-Disposition", "attachment; filename=\"${SourceDefinition.Instance.code}-${localFile.name}\"")
        context.respondFile(localFile)
    } else {
        context.respond(HttpStatusCode.NotFound)
    }
}
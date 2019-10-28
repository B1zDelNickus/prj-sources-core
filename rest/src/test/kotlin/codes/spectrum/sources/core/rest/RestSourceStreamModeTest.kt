package codes.spectrum.sources.core.rest

import codes.spectrum.api.SourceState
import codes.spectrum.sources.*
import codes.spectrum.sources.config.IConfig
import codes.spectrum.sources.core.model.IResponseConfigContext
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Transient
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import kotlin.reflect.full.createInstance

data class FakeContext(
    val test: String = "test"
)
    : SourceContext<String, String>(
    query = SourceQuery(query = "query"),
    result = SourceResult(data = "123")
), IResponseConfigContext {
    override var responseConfig: SourceResponseConfig
        = SourceResponseConfig(contentType = "text/plain", responseType = SourceResponseType.STREAM)
    
    override fun outputStream(outputStream: OutputStream) {
        this.outputStream = outputStream
    }
    
    @Transient
    @kotlin.jvm.Transient
    var outputStream: OutputStream? = null
}

class RestSourceStreamModeTest : StringSpec({

    
    class FakeStreamHandler : ISourceHandler<FakeContext> {
        override suspend fun execute(context: FakeContext, config: IConfig) {
            if (!context.responseConfig.respondAsStream()) throw RuntimeException("Must work only with streams")
            context.result.data += "_executed on ${context.query.query}"
            context.outputStream!!.write(context.result.data.toByteArray(Charsets.UTF_8))
            context.outputStream!!.flush()
        }
    }
    
    class FakeAdapter: RestSourceAdapter<String, String>(FakeStreamHandler(), FakeContext::class.java) {
        override suspend fun prepareContextFromHttpRequest(context: SourceContext<String, String>, pipeline: PipelineContext<*, ApplicationCall>) {
            context.query.query += "_example"
        }
    }
    
    fun Application.main() {
        setupNegotiation()
        routing {
            get("stream") {
                runWithStateInfo { FakeAdapter().execute(this) }
            }
            get("direct") {
                runWithStateInfo {
                    FakeAdapter().execute(this) {
                        this as FakeContext
                        this.responseConfig = SourceResponseConfig("application/json")
                    }
                }
            }
        }
    }
    
    "context writes to stream successfully" {
        val outerContext = mock<PipelineContext<String, ApplicationCall>>()
        val ostream = ByteArrayOutputStream(1024)
        val adapter = FakeAdapter()
        val adapterSpy = spy(adapter)
        doAnswer {
            val internalcontext = FakeContext::class.java.kotlin.createInstance()
            runBlocking { adapterSpy.prepareContextFromHttpRequest(internalcontext, it.getArgument(0)) }
    
            if (adapterSpy.getResponseConfig(internalcontext).respondAsStream()) {
                internalcontext.outputStream(ostream)
                runBlocking { FakeStreamHandler().safeExecute(internalcontext) }
            } else {
                throw IllegalArgumentException("We are only testing stream mode here")
            }
            SourceState.OK
        }.`when`(adapterSpy).execute(any(), any())
        
        val r = adapterSpy.execute(outerContext)
        val actualResult = String(ostream.toByteArray(), Charsets.UTF_8)
        val expectedResult = "123_executed on query_example"
        actualResult shouldBe expectedResult
        r shouldBe SourceState.OK
    }
    
    "app call processes correctly" {
        val expectedResult = "123_executed on query_example"
        withTestApplication(Application::main) {
            with(handleRequest(HttpMethod.Get, "stream")) {
                response.status()?.value shouldBe SourceState.OK.code
                val bytes = ByteArray(expectedResult.length)
                runBlocking { response.contentChannel()?.readAvailable(bytes, 0, expectedResult.length) }
                String(bytes, Charsets.UTF_8) shouldBe expectedResult
            }
        }
    }
    
    "error if used in direct (non-stream) mode" {
        withTestApplication(Application::main) {
            val oldErrors = StateInfo.Instance.sourceErrors.get()
            with(handleRequest(HttpMethod.Get, "direct") {
                addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }) {
                response.status()?.value shouldNotBe SourceState.OK.code
                StateInfo.Instance.sourceErrors.get() shouldBe oldErrors + 1
            }
        }
    }
})
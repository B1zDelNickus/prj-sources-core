package codes.spectrum.sources.core.client

import codes.spectrum.serialization.json.Json
import codes.spectrum.sources.ISourceHandler
import codes.spectrum.sources.SourceContext
import codes.spectrum.sources.config.IConfig
import codes.spectrum.sources.core.SourceDefinition
import codes.spectrum.api.SourceState
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import java.net.URL
import java.nio.charset.Charset

abstract class ClientBase<Q, R>(
    val baseUrl: String = "http://127.0.0.1:8080",
    val apiRoot: String = SourceDefinition.Instance.serviceApiRoot,
    val command: String = "/execute",
    val httpConfig: ApacheHttpConfig = ApacheHttpConfig(),
    httpClient: HttpClient? = null
) : ISourceHandler<SourceContext<Q, R>> {
    protected val client = httpClient ?: HttpClient(Apache){
        this.engine {
            this.followRedirects = httpConfig.followRedirects
            this.socketTimeout = httpConfig.socketTimeout
            this.connectTimeout = httpConfig.connectTimeout
            this.connectionRequestTimeout = httpConfig.connectionRequestTimeout
        }
    }

    open val clazz: Class<out SourceContext<Q, R>>? = null

    override suspend fun execute(context: SourceContext<Q, R>, config: IConfig) {
        try {
            val body = Json.stringify(context.query)
            val contentType = ContentType.Application.Json.withCharset(Charset.forName("utf-8"))
            var json = client.post<String>(
                URL("${baseUrl}${apiRoot}${command}")
            ) {
                this.body = TextContent(body, contentType)
            }
            var income_context = Json.read(json, clazz ?: context.javaClass)
            context.result = income_context.result
        } catch (e: Exception) {
            context.result.status = SourceState.CLIENT_ERROR
            context.result.error = e
        }

    }
}



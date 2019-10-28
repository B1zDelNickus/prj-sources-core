package codes.spectrum.sources.core.rest

import codes.spectrum.serialization.json.Json
import codes.spectrum.sources.json.JsonHash
import codes.spectrum.sources.json.JsonifyOptions
import codes.spectrum.sources.json.jset
import codes.spectrum.sources.json.jsetDefault
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.request.httpMethod
import io.ktor.request.receiveText
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.toMap
import org.slf4j.LoggerFactory

class InputDataParser {
    val logger = LoggerFactory.getLogger(this::class.java)
    suspend fun <T> parseInput(clz: Class<T>, context: PipelineContext<*, ApplicationCall>, vararg defaultPrefixes: String): T {
        return parseInput(clz, {
            if (context.call.request.httpMethod == HttpMethod.Post) {
                context.context.receiveText()
            } else "{}"
        }, { context.context.parameters.toMap() }, *defaultPrefixes)
    }

    suspend fun <T> parseInput(clz: Class<T>, textReciever: suspend () -> String = { "{}" }, mapReciever: suspend () -> Map<*, *> = { mapOf<String, Any?>() },  vararg defaultPrefixes: String): T {
        val jsonbase = Json.read<JsonHash>(textReciever())
        for(e in jsonbase.entries.toList()){
            if(e.key.startsWith("$")){
                jsonbase.remove(e.key)
            }
        }
        val firstlevel = jsonbase.entries.toList()

        for(prefix in defaultPrefixes){
            if(!jsonbase.containsKey(prefix)){
                for(fl in firstlevel){
                    jsonbase.jsetDefault("${prefix}.${fl.key}",fl.value)
                }
            }
        }
        for (p in mapReciever().entries) {
            jsonbase.jset(p.key.toString(), p.value, JsonifyOptions.HttpQuery)
            if (!p.key.toString().contains(".")) {
                for (prefix in defaultPrefixes) {
                    jsonbase.jsetDefault("${prefix}." + p.key, p.value, JsonifyOptions.HttpQuery)
                }
            }
        }
        val mergedJson = Json.stringify(jsonbase)
        logger.debug("Merged json in: ${mergedJson} prefixed with ${defaultPrefixes.joinToString()}")
        return Json.read(mergedJson, clz)

    }

    companion object {
        val Instance = InputDataParser()
    }
}
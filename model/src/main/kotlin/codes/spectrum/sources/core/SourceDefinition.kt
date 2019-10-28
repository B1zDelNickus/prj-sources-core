package codes.spectrum.sources.core

import codes.spectrum.serialization.json.Json
import codes.spectrum.sources.config.EnvProxy
import codes.spectrum.sources.config.IConfig
import codes.spectrum.sources.config.getOrDefault
import java.io.File


data class SourceDefinition (
    /**
     * Код источника
     */
    val code: String = "",

    /**
     * Название источника
     */
    val name: String = "",
    val librarySet: Boolean = false,
    val className: String? = null,

    /**
     * Имя класса провайдера
     */
    val sourceClazzName: String = DEFAULT_SOURCE_CLASS_NAME,

    /**
     * Имя класса контекста
     */
    val contextClazzName: String = DEFAULT_CONTEXT_CLASS_NAME,

    /**
     * Имя класса request
     */
    val requestClazzName: String = DEFAULT_REQUEST_CLASS_NAME,

    /**
     * Имя класса результата
     */
    val resultClazzName: String = DEFAULT_RESULT_CLASS_NAME,

    /**
     * Имя класса запроса
     */
    val queryClazzName: String = DEFAULT_QUERY_CLASS_NAME

){
    val serviceApiRoot get() = "/api/v1/sources/${code}/cli"

    val agentApiRoot get() = "/api/v1/sources/${code}/agent"

    val packageName get() = code.replace("-",".")

    val serviceLoggerName get() = "source-${code}-service"

    val aggentLoggerName  get() = "source-${code}-agent"

    val classNamePrefix get() =
        if(className.isNullOrBlank()) code.replace("""(_|$|-|\.)(\w)""".toRegex()) {
            it.groups[1]!!.value.toUpperCase()
        }
        else
            className

    val devHostName get() = "https://dev-${code.replace("""[_.\\/]""".toRegex(), "-")}-sources.spectrum.codes/"

    val prodHostName get() = "https://${code.replace("""[_.\\/]""".toRegex(), "-")}-sources.spectrum.codes/"

    companion object {

        /**
         * sourceClazzName по умолчанию
         */
        const val DEFAULT_SOURCE_CLASS_NAME: String = "codes.spectrum.sources.ISourceHandler"

        /**
         * contextClazzName по умолчанию
         */
        const val DEFAULT_CONTEXT_CLASS_NAME: String = "codes.spectrum.sources.SourceContext"

        /**
         * requestClazzName по умолчанию
         */
        const val DEFAULT_REQUEST_CLASS_NAME: String = "codes.spectrum.sources.SourceQuery"

        /**
         * resultClazzName по умолчанию
         */
        const val DEFAULT_RESULT_CLASS_NAME: String = "codes.spectrum.sources.SourceResult"

        /**
         * queryClazzName по умолчанию
         */
        const val DEFAULT_QUERY_CLASS_NAME: String = "codes.spectrum.sources.core.model.IQuery"

        val SourceDefinitionFileEnvVariable = "SOURCE_DEFINITION_FILE"

        val SourceDefinitionJsonEnvVariable = "SOURCE_DEFINITION_JSON"

        fun load(config: IConfig = EnvProxy.Instance):SourceDefinition{
            val probes = arrayOf(
                config.getOrDefault(SourceDefinitionJsonEnvVariable,""),
                config.getOrDefault(SourceDefinitionFileEnvVariable,""),
                "./source.json",
                "./src/main/resources/source.json",
                "../source.json",
                "classpath:source.json"
            )
            var content = ""
            for(p in probes) {
                if (p.isBlank()) continue
                if (p.startsWith("{")) {
                    content = p
                } else if(p.startsWith("classpath:")) {
                    content = ClassLoader.getSystemResourceAsStream(p.replace("classpath:","")).reader().readText()
                } else {
                    if (File(p).exists()) {
                        content = File(p).readText()
                    }
                }
                if (content.isNotBlank()) {
                    break
                }
            }
            return Json.read<SourceDefinition>(content)
        }
        val Instance: SourceDefinition by lazy { load() }
    }
}
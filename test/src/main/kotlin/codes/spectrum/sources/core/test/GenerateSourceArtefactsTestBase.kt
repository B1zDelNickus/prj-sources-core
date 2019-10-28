package codes.spectrum.sources.core.test

import codes.spectrum.sources.core.SourceDefinition
import codes.spectrum.sources.core.source.SourceDescriptor
import codes.spectrum.sources.core.test.UIGeneratorConstants.GENERATE_ENV
import codes.spectrum.sources.core.test.UIGeneratorConstants.INSOMNIA_EXPORT_TIME_ENV
import codes.spectrum.sources.core.test.UIGeneratorConstants.INSOMNIA_JSON_PATH
import codes.spectrum.sources.core.test.UIGeneratorConstants.INSOMNIA_YAML_PATH
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_HTML_PATH
import codes.spectrum.sources.core.test.UIGeneratorExtentions.docLink
import codes.spectrum.sources.core.test.UIGeneratorExtentions.insomniaLink
import codes.spectrum.sources.core.test.UIGeneratorExtentions.rowDiv
import codes.spectrum.sources.core.test.UIGeneratorExtentions.sourceTitle
import codes.spectrum.sources.core.test.UIGeneratorExtentions.writeBody
import codes.spectrum.sources.core.test.UIGeneratorExtentions.writeDoc
import codes.spectrum.sources.core.test.UIGeneratorExtentions.writeHead
import io.kotlintest.specs.StringSpec
import org.joda.time.Instant
import java.io.File

open class GenerateSourceArtefactsTestBase(
    sourceDescriptor: SourceDescriptor,
    isTestEnabled: Boolean = (System.getenv(GENERATE_ENV) ?: "true").toBoolean(),
    body: GenerateSourceArtefactsTestBase.() -> Unit = {}
) : StringSpec({
    //Генерация Insomnia проекта
    "${SourceDefinition.Instance.code} generate insomnia json" {
        if (isTestEnabled) {
            val timeString = System.getenv(INSOMNIA_EXPORT_TIME_ENV)
            val time =
                if (timeString != null)
                    Instant.parse(timeString)
                else Instant.now()
            val rootObject = InsomniaGeneratorExtentions.getInsomniaObject(sourceDescriptor, time)
            File(INSOMNIA_JSON_PATH).writer().use { it.appendln(rootObject.toJson()) }
            File(INSOMNIA_YAML_PATH).writer().use { it.appendln(rootObject.toYaml()) }
        }
    }
    //Генерация ui.html
    "${SourceDefinition.Instance.code} generate ui.html" {
        if (isTestEnabled) {
            File(UI_HTML_PATH).writer().use {
                with(it) {
                    writeDoc { docIndent ->
                        writeHead(docIndent) {}
                        writeBody(docIndent) { indent ->
                            rowDiv(indent) { rowIndent ->
                                sourceTitle(indent = rowIndent)
                            }
                            rowDiv(indent) { rowIndent ->
                                insomniaLink(indent = rowIndent)
                            }
                            rowDiv(indent) { rowIndent ->
                                docLink(indent = rowIndent)
                            }
                            rowDiv(indent) { rowIndent ->
                                appendln(UIGeneratorExtentions.tag("div", attrs = mapOf("id" to "source-page", "class" to "col"), indent = rowIndent, emptyContent = true))
                            }
                            appendln(UIGeneratorExtentions.reactScript(sourceDescriptor, indent))
                        }
                    }
                }
            }
        }
    }

    //Дополнительные тесты
    (this as GenerateSourceArtefactsTestBase).body()
})
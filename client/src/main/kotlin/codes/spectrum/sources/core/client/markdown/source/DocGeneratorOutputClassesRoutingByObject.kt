package codes.spectrum.sources.core.client.markdown.source

import codes.spectrum.sources.core.client.markdown.DocGenerator
import codes.spectrum.sources.core.client.markdown.schemaDesc.DocumentationSchemaDescription
import codes.spectrum.utils.json.JsonHash

fun DocGenerator.outputClassRouting(schemaDesc: DocumentationSchemaDescription<*>) {
    paragraph {
        schemaDesc.classDesc.forEach { classDesc ->
            header(classDesc.clazz) {
                paragraph {
                    add(classDesc.description ?: "")
                }

                fields(classDesc.fields)
            }
        }
    }

    paragraph {
        schemaDesc.enumDesc.forEach { enumDesc ->
            header(enumDesc.className) {
                paragraph {
                    startTable("Поле", "Значение")
                    enumDesc.values.forEach { (key, value) ->
                        row("`$key`", value)
                    }
                }
            }
        }
    }

    header("Пример ответа") {
        code(JsonHash(schemaDesc.obj))
    }
}

fun <T : Any> DocGenerator.outputClassRouting(obj: T, populate: DocumentationSchemaDescription.Builder<T>.() -> Unit) = this.outputClassRouting(DocumentationSchemaDescription(obj, populate))
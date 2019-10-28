package codes.spectrum.sources.core.client.markdown.source

import codes.spectrum.sources.core.client.markdown.DocGenerator
import codes.spectrum.sources.core.client.markdown.schemaDesc.DocumentationSchemaDescription
import codes.spectrum.sources.core.client.markdown.schemaDesc.FieldDescription
import codes.spectrum.utils.json.JsonHash

fun DocGenerator.outputRouting(schemaDesc: DocumentationSchemaDescription<*>) {
    fields(schemaDesc.fieldDesc)

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

fun <T : Any> DocGenerator.outputRouting(obj: T, populate: DocumentationSchemaDescription.Builder<T>.() -> Unit) = this.outputRouting(DocumentationSchemaDescription(obj, populate))

fun DocGenerator.fields(fields: List<FieldDescription>) {
    paragraph {
        val formatColumn = fields.any { it.format != null || it.ref != null }

        if (formatColumn) {
            +"|Поля|Описание|Тип значений|Формат|Пример|"
            +"|---|---|:---:|:---:|---|"
        } else {
            +"|Поля|Описание|Тип значений|Пример|"
            +"|---|---|:---:|---|"
        }

        fields.forEach { field ->
            val refType = "`${field.type.ifBlank { "object" }}`"
            val example = when {
                field.example == null -> ""
                field.type == "string" -> "`\"${field.example}\"`"
                else -> "`${field.example}`"
            }
            val exFormat = if (formatColumn)
                listOf(when {
                    field.format != null -> "`${field.format}`"
                    field.ref != null -> "[`${field.ref}`](#${field.ref.toLowerCase()})"
                    else -> " "
                })
            else listOf()
            row(listOf("`${field.field}`", field.description, refType) + exFormat + listOf(example))
        }
    }
}
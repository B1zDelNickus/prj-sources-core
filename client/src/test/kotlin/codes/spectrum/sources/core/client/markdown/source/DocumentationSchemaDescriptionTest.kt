package codes.spectrum.sources.core.client.markdown.source

import codes.spectrum.schemaDesc.DocFormat
import codes.spectrum.schemaDesc.IDocTitle
import codes.spectrum.serialization.jsonschema.JsonSchemaId
import codes.spectrum.serialization.jsonschema.JsonSchemaIndex
import codes.spectrum.serialization.jsonschema.JsonSchemaTitle
import codes.spectrum.sources.core.client.markdown.DocGenerator
import codes.spectrum.sources.core.client.markdown.schemaDesc.DocumentationSchemaDescription
import io.kotlintest.specs.StringSpec

class DocumentationSchemaDescriptionTest : StringSpec({
    val example = SimpleClass("Стена", Color.G, Order.Second)
    val obj = DocumentationSchemaDescription(example) {
        Color.R to "Красный"
        Color.G to "Зеленый"
        Color.B to "Синий"
    }

    "Простой объект" {
        println(obj)
    }
    "Классовая документация по объекту" {
        println(DocGenerator { outputClassRouting(obj) })
    }
    "Полевая документация по объекту" {
        println(DocGenerator { outputRouting(obj) })
    }
}) {
    @JsonSchemaId("simple_class")
    data class SimpleClass(
            @JsonSchemaIndex(1)
            @JsonSchemaTitle("Заголовок")
            @DocFormat("???")
            val title: String,
            @JsonSchemaIndex(2)
            @JsonSchemaTitle("Цвет")
            val enum: Color,
            @JsonSchemaIndex(3)
            @JsonSchemaTitle("Порядок")
            val order: Order
    )

    enum class Color {
        A, R, G, B
    }

    enum class Order(override val docTitle: String) : IDocTitle {
        First("Первый"),
        Second("Второй")
    }
}
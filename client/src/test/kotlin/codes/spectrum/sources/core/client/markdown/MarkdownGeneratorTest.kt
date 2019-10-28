package codes.spectrum.sources.core.client.markdown

import codes.spectrum.utils.json.JsonHash
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class MarkdownGeneratorTest() : StringSpec({
    "gen" {
        val doc = DocGenerator().apply {
            header("Заголовок №1") {
                header("Заголовок №2") {
                    header("Заголовок №3") {
                        paragraph {
                            add("Какие-то данные")
                        }
                    }
                    header("Таблица") {
                        paragraph {
                            startTable("Номер", "Первая колонка", "Вторая колонка")
                            row(1, "Иванов Петр Ильич", true)
                            row(2, "Петренко", false)
                            row(3, "Анонимус", null)
                        }
                    }
                    header("Какой-то JSON") {
                        code(JsonHash.parse("""{"a":"b","c":["d"]}"""))
                    }
                }
            }
        }
        doc.toString().trimIndent() shouldBe """
# Заголовок №1

## Заголовок №2

### Заголовок №3

Какие-то данные

### Таблица

|Номер|Первая колонка|Вторая колонка|
|---|---|---|
|1|Иванов Петр Ильич|Да|
|2|Петренко|Нет|
|3|Анонимус|` - `|

### Какой-то JSON

```json
{
  "a": "b",
  "c": [
    "d"
  ]
}
```
""".trimIndent()
    }
})
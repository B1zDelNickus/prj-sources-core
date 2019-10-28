package codes.spectrum.sources.core.client.markdown

import codes.spectrum.sources.core.client.markdown.source.inputRouting
import codes.spectrum.sources.core.client.markdown.source.outputRouting
import codes.spectrum.utils.json.JsonHash
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class SourceRoutingMarkdownGeneratorTest() : StringSpec({
    "gen" {
        val doc = DocGenerator().apply {
            val query = JsonHash.parse("""{"queryType":"MULTIPART","query":" ","data":{"first_name":"Иван","patronymic":"Иванович","last_name":"Иванов"}}""")

            val response = JsonHash.parse("""{"found":true,"source":{"count":2,"items":[{"status":"OK"},{"status":"WARN"}]},"nullable":null}""")

            header("Входные данные") {
                header("Описание полей") {
                    inputRouting(query) {
                        field("queryType", "Тип запроса", "String", true)
                        field("query", "Запрос", "String", true)

                        route("data", "Параметры запроса") {
                            field("last_name", "Фамилия", "String", true)
                            field("first_name", "Имя", "String", true)
                            field("patronymic", "Отчество", "String")
                        }
                    }
                }

                header("Пример") {
                    code(query)
                }
            }

            header("Выходные данные") {
                header("Описание полей") {
                    outputRouting(response) {
                        field("found", "Данные получены", "Boolean")
                        route("source", "Источник") {
                            field("count", "Кол-во", "Integer")
                            route("items[]", "Элементы") {
                                field("status", "Статус", "String")
                            }
                        }
                        field("nullable", "NULL - какой-то", "String")
                    }
                }

                header("Пример") {
                    code(response)
                }
            }
        }
        doc.toString().trimIndent() shouldBe """
# Входные данные

## Описание полей

|Путь до поля|Описание|Тип значений|Пример|Обязательность|
|---|---|---|---|---|
|queryType|Тип запроса|String|MULTIPART|Да|
|query|Запрос|String| |Да|
|data.last_name|Параметры запроса. Фамилия|String|Иванов|Да|
|data.first_name|Параметры запроса. Имя|String|Иван|Да|
|data.patronymic|Параметры запроса. Отчество|String|Иванович|Нет|

## Пример

```json
{
  "queryType": "MULTIPART",
  "query": " ",
  "data": {
    "first_name": "Иван",
    "patronymic": "Иванович",
    "last_name": "Иванов"
  }
}
```

# Выходные данные

## Описание полей

|Путь до поля|Описание|Тип значений|Пример|
|---|---|---|---|
|found|Данные получены|Boolean|Да|
|source.count|Источник. Кол-во|Integer|2|
|source.items[].status|Источник. Элементы. Статус|String|OK|
|nullable|NULL - какой-то|String|` - `|

## Пример

```json
{
  "found": true,
  "source": {
    "count": 2,
    "items": [
      {
        "status": "OK"
      },
      {
        "status": "WARN"
      }
    ]
  }
}
```
""".trimIndent()
    }
})
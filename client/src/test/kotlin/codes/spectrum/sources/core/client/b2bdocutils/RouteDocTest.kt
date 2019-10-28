package codes.spectrum.sources.core.client.b2bdocutils

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class RouteDocTest : StringSpec({
    "test routing" {
        val doc = b2bDoc {
            `new query` {
                `query example` {
                    body {
                        this `query type` "MULTIPART"
                        this query " "
                        this data QueryExampleBodyTest()
                    }
                }
            }

            `get report` {
                output {
                    route("data[]") {
                        route("query.data", "Заявитель") {
                            item {
                                this name "last_name"
                                this values "String"
                                this description "Фамилия"
                                this example "Иванов"
                            }

                            item {
                                this name "first_name"
                                this values "String"
                                this description "Имя"
                                this example "Иван"
                            }

                            item {
                                this name "patronymic"
                                this values "String"
                                this description "Отчество"
                                this example "Иванович"
                            }
                        }

                        route("content") {
                            route("check_person") {
                                item {
                                    this name "field_one"
                                    this values "Integer (разные значения)"
                                    this description "Кол-во проверок"
                                    this example "2"
                                }
                            }
                        }
                    }
                }
            }
        }
        B2bSourceDocCreator.create(doc).trimIndent().trim() shouldBe """
# Блок: 

## Создание нового запроса

##### **URL:** https://b2b-api.checkperson.ru/b2b/api/v1/user/reports/{REPORT_TYPE_UID}/_make
##### **Назначение:**  Создание нового запроса
##### **Тип запроса:** POST
##### **Кодировка:** UTF-8
##### **Входные параметры:**

<table border=1>
<tr>
    <th>Название</th>
    <th>Обязательность</th>
    <th>Допустимые значения</th>
    <th>Описание</th>
    <th>Пример</th>
</tr><tr>
    <td align="center">Authorization</td>
    <td align ="center"><b>+</b></td>
    <td align="center">String</td>
    <td align="center">Токен авторизации</td>
    <td align="center">AR-REST Ge4tffdsg4gdfv6gf10</td>
</tr>
<tr>
    <td align="center">REPORT_TYPE_UID</td>
    <td align ="center"><b>+</b></td>
    <td align="center">String</td>
    <td align="center">Уникальный идентификатор типа отчета</td>
    <td align="center">report_type_name@domain_name</td>
</tr>
<tr>
    <td align="center">queryType</td>
    <td align ="center"><b>+</b></td>
    <td align="center">String</td>
    <td align="center">Тип запроса</td>
    <td align="center">MULTIPART</td>
</tr>
<tr>
    <td align="center">query</td>
    <td align ="center"><b>+</b></td>
    <td align="center">String</td>
    <td align="center">Значение простого запроса</td>
    <td align="center">пустое значение</td>
</tr>

</table>

##### **Параметры ответа:**

<table border=1>
<tr>
    <th>Название</th>
    <th>Допустимые значения</th>
    <th>Описание</th>
    <th>Пример</th>
</tr><tr>
    <td align="center">state</td>
    <td align="center">String</td>
    <td align="center">Состояние работы источника</td>
    <td align="center">ok</td>
</tr>
<tr>
    <td align="center">size</td>
    <td align="center">Number (Integer)</td>
    <td align="center">Кол-во элементов в массиве data</td>
    <td align="center">1</td>
</tr>
<tr>
    <td align="center">stamp</td>
    <td align="center">String</td>
    <td align="center">Дата создания запроса</td>
    <td align="center">2018-11-20T11:06:37.785Z</td>
</tr>
<tr>
    <td align="center">data[].uid</td>
    <td align="center">String</td>
    <td align="center">Уникальный идентификатор отчета</td>
    <td align="center">some_report_uid</td>
</tr>
<tr>
    <td align="center">data[].isnew</td>
    <td align="center">Boolean</td>
    <td align="center">Признак создания нового отчета</td>
    <td align="center">true</td>
</tr>

</table>

##### **Пример запроса:**
###### URL: https://b2b-api.checkperson.ru/b2b/api/v1/user/reports/{REPORT_TYPE_UID}/_make
###### Method: POST
###### Header: Authorization = AR-REST Ge4tffdsg4gdfv6gf10
###### Body:

```json
{
  "queryType": "MULTIPART",
  "query": " ",
  "data": {
    "passport": "1234 123456",
    "first_name": "Иван",
    "last_name": "Иванов"
  }
}

```

##### **Пример ответа:**

```json
""

```

##### **Пример ответа с ошибкой:**

```json
{
  "state": "fail",
  "stamp": "2018-11-20T12:43:17.065Z",
  "event": {
    "uid": "",
    "stamp": "2018-11-20T12:43:17.062Z",
    "cls": "Data",
    "type": "DataSeekObjectError",
    "name": "Отсутствие объекта с заданным идентификатором",
    "message": "Отсутствует объект типа api.model.Report_Type с UID some_report_type_uid@some_domain",
    "data": {
      "entity_type": "api.model.Report_Type",
      "entity_uid": "some_report_type_uid@some_domain"
    },
    "events": []
  }
}

```

----------------------------------------------------
## Получение отчета

##### **URL:** https://b2b-api.checkperson.ru/b2b/api/v1/user/reports/{REPORT_DESC}?_content=true
##### **Назначение:**  Получение отчета
##### **Тип запроса:** GET
##### **Кодировка:** UTF-8
##### **Входные параметры:**

<table border=1>
<tr>
    <th>Название</th>
    <th>Обязательность</th>
    <th>Допустимые значения</th>
    <th>Описание</th>
    <th>Пример</th>
</tr><tr>
    <td align="center">Authorization</td>
    <td align ="center"><b>+</b></td>
    <td align="center">String</td>
    <td align="center">Токен авторизации</td>
    <td align="center">AR-REST Ge4tffdsg4gdfv6gf10</td>
</tr>
<tr>
    <td align="center">REPORT_DESC</td>
    <td align ="center"><b>+</b></td>
    <td align="center">String</td>
    <td align="center">Уникальный идентификатор отчета</td>
    <td align="center">some_report_uid</td>
</tr>
<tr>
    <td align="center">_content</td>
    <td align ="center"><b>+</b></td>
    <td align="center">Boolean</td>
    <td align="center">Показать контент отчета</td>
    <td align="center">true</td>
</tr>

</table>

##### **Параметры ответа:**

<table border=1>
<tr>
    <th>Название</th>
    <th>Допустимые значения</th>
    <th>Описание</th>
    <th>Пример</th>
</tr><tr>
    <td align="center">state</td>
    <td align="center">String</td>
    <td align="center">Статус обработки запроса</td>
    <td align="center">ok</td>
</tr>
<tr>
    <td align="center">size</td>
    <td align="center">Number (Integer)</td>
    <td align="center">Кол-во элементов в массиве data</td>
    <td align="center">1</td>
</tr>
<tr>
    <td align="center">stamp</td>
    <td align="center">String</td>
    <td align="center">Дата создания запроса</td>
    <td align="center">2018-11-20T11:06:37.785Z</td>
</tr>
<tr>
    <td align="center">data[].domain_uid</td>
    <td align="center">String</td>
    <td align="center">Уникальный идентификатор домена</td>
    <td align="center">some_domain_uid</td>
</tr>
<tr>
    <td align="center">data[].report_type_uid</td>
    <td align="center">String</td>
    <td align="center">Уникальный идентификатор типа отчета</td>
    <td align="center">some_report_type_uid</td>
</tr>
<tr>
    <td align="center">data[].progress_ok</td>
    <td align="center">Number (Integer)</td>
    <td align="center">Кол-во источников в выполненном состоянии</td>
    <td align="center">4</td>
</tr>
<tr>
    <td align="center">data[].progress_wait</td>
    <td align="center">Number (Integer)</td>
    <td align="center">Кол-во источников в процессе выполнения</td>
    <td align="center">2</td>
</tr>
<tr>
    <td align="center">data[].progress_error</td>
    <td align="center">Number (Integer)</td>
    <td align="center">Кол-во источников завершившихся ошибкой</td>
    <td align="center">0</td>
</tr>
<tr>
    <td align="center">data[].state.sources[]._id</td>
    <td align="center">String</td>
    <td align="center">Название источника</td>
    <td align="center">some_source_name</td>
</tr>
<tr>
    <td align="center">data[].state.sources[].state</td>
    <td align="center">String (OK, ERROR, PROGRESS, SKIP)</td>
    <td align="center">Состояние источника</td>
    <td align="center">OK</td>
</tr>
<tr>
    <td align="center">data[].query.type</td>
    <td align="center">String</td>
    <td align="center">Тип запроса</td>
    <td align="center">MULTIPART</td>
</tr>
<tr>
    <td align="center">data[].query.data.last_name</td>
    <td align="center">String</td>
    <td align="center">Заявитель. Фамилия</td>
    <td align="center">Иванов</td>
</tr>
<tr>
    <td align="center">data[].query.data.first_name</td>
    <td align="center">String</td>
    <td align="center">Заявитель. Имя</td>
    <td align="center">Иван</td>
</tr>
<tr>
    <td align="center">data[].query.data.patronymic</td>
    <td align="center">String</td>
    <td align="center">Заявитель. Отчество</td>
    <td align="center">Иванович</td>
</tr>
<tr>
    <td align="center">data[].content.check_person.field_one</td>
    <td align="center">Integer (разные значения)</td>
    <td align="center">Кол-во проверок</td>
    <td align="center">2</td>
</tr>

</table>

##### **Пример запроса:**
###### URL: https://b2b-api.checkperson.ru/b2b/api/v1/user/reports/{REPORT_UID}?_content=true
###### Method: GET
###### Header: Authorization = AR-REST Ge4tffdsg4gdfv6gf10

##### **Пример ответа:**

```json
{
  "state": "ok",
  "size": 1,
  "stamp": "2018-11-20T13:59:27.141Z",
  "data": []
}

```""".trimIndent().trim()
    }
})
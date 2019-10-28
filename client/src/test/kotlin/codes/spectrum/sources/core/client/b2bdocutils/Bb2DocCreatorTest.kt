package codes.spectrum.sources.core.client.b2bdocutils

import codes.spectrum.serialization.json.Json
import codes.spectrum.utils.json.JsonHash
import io.kotlintest.specs.StringSpec


class Bb2DocCreatorTest: StringSpec({

    "!b2b doc dsl"{
        val b2bDoc = b2bDoc {
            this + "src/test/resources/test_doc.md"

            header {
                this prefix "Блок"
                this name "Тестовый источник"
            }

            `new query` {
                this url """https://b2b-api.checkperson.ru/b2b/api/v1/user/reports/{REPORT_TYPE_UID}/_make"""
                this destination "Создание нового запроса"
                this type "POST"
                this encoding "UTF-8"

                input {
                    item {
                        this name "data.first_name"
                        this required true
                        this values "String"
                        this description "Имя"
                        this example "Иван"
                    }
                }

                `query example` {
                    this url """https://b2b-api.checkperson.ru/b2b/api/v1/user/reports/{REPORT_TYPE_UID}/_make"""
                    this method "POST"
                    this header "Authorization = AR-REST Ge4tffdsg4gdfv6gf10"

                    body {
                        this `query type` "MULTIPART"
                        this query " "
                        this data QueryExampleBodyTest()
                    }
                }

                `response example` {

                    body {
                        this state "ok"
                        this size 1
                        this stamp "2018-12-20T11:06:37.785Z"

                        data {
                            this uid "some_report_uid@some_domain_uid"
                            this isnew true
                            this `process request uid` "some_process_request_uid"
                            this `suggest get` "2018-11-20T11:06:37.569Z"
                        }
                    }
                }

                `response fail` {

                    this state "fail"
                    this stamp "2018-11-20T12:43:17.065Z"

                    event {

                        this uid ""
                        this stamp "2018-11-20T12:43:17.062Z"
                        this cls "Data"
                        this type "DataSeekObjectError"
                        this name "Отсутствие объекта с заданным идентификатором"
                        this message "Отсутствует объект типа api.model.Report_Type с UID some_report_type_uid@some_domain"

                        data {

                            this `entity type` "api.model.Report_Type"
                            this `entity uid` "some_report_type_uid@some_domain"
                        }
                    }
                }
            }

            `get report` {
                this url """https://b2b-api.checkperson.ru/b2b/api/v1/user/reports/{REPORT_DESC}?_content=true"""
                this destination "Получение отчета"
                this type "GET"
                this encoding "UTF-8"

                output {
                    item {
                        this name "data[].query.data.last_name"
                        this values "String"
                        this description "Фамилия"
                        this example "Иванов"
                    }

                    item {
                        this name "data[].query.data.first_name"
                        this values "String"
                        this description "Имя"
                        this example "Иван"
                    }

                    item {
                        this name "data[].query.data.patronymic"
                        this values "String"
                        this description "Отчество"
                        this example "Иванович"
                    }

                    item {
                        this name "data[].content.check_person.field_one"
                        this values "Integer (разные значения)"
                        this description "Кол-во проверок"
                        this example "2"
                    }
                }

                `query example` {
                    this url """https://b2b-api.checkperson.ru/b2b/api/v1/user/reports/{REPORT_UID}?_content=true"""
                    this method "GET"
                    this header "Authorization = AR-REST Ge4tffdsg4gdfv6gf10"
                }

                `response example` {
                    this state "ok"
                    this size 1
                    this stamp "2018-11-20T13:59:27.141Z"

                    item {
                        this `domain uid` "some_domain_uid"
                        this `report type uid` "some_report_type_uid@some_domain_uid"
                        query {
                            this type "MULTIPART"
                            this body " "
                            this data JsonHash(mutableMapOf("passport" to "1234 123456", "first_name" to "Иван"))
                        }

                        this content GetResponseExampleDataContent(
                                GetResponseExampleDataContentTest()
                        )

                    }
                }
            }
        }

        println(Json.stringify(b2bDoc, true))
        b2bDoc.createMd()
        b2bDoc.createSimpleJson()
    }

})
package codes.spectrum.sources.core.client.b2bdocutils

import codes.spectrum.konveyor.konveyor
import codes.spectrum.serialization.json.Json
import kotlinx.coroutines.runBlocking

data class B2bDocSimpleJson(
        var path: String = "",
        var description: String = "",
        var types: MutableList<String> = mutableListOf(),
        var example: String = ""
)

object B2bSourceDocCreator {

    const val CONTENT = "data[].content."

    fun create(doc: B2bSourceDoc): String {
        val context = B2bSourceDocContext(doc)
        runBlocking { conveyor.exec(context) }
        return context.result
    }

    fun createSimpleJson(doc: B2bSourceDoc): String {
        val simpleJsons = mutableListOf<B2bDocSimpleJson>()
        doc.get_report.output.output_params.filter { it.name.startsWith(CONTENT) }.forEach {
            val path = it.name.split(CONTENT)[1]
            val description = it.description
            val type = if(it.values.contains(" ")) it.values.split(" ")[0] else it.values
            val example = it.example

            simpleJsons.add(B2bDocSimpleJson(path, description, mutableListOf(type), example))
        }
        return Json.stringify(simpleJsons, true)
    }

    fun createQueryInputTableHeader(queryHeader: NewQueryInputParamsHeader): String {
        return """
            <tr>
                <th>${queryHeader.name}</th>
                <th>${queryHeader.required}</th>
                <th>${queryHeader.values}</th>
                <th>${queryHeader.description}</th>
                <th>${queryHeader.example}</th>
            </tr>
        """.trimIndent()
    }

    fun getReportInputTableHeader(reportHeader: GetReportInputParamsHeader): String {
        return """
            <tr>
                <th>${reportHeader.name}</th>
                <th>${reportHeader.required}</th>
                <th>${reportHeader.values}</th>
                <th>${reportHeader.description}</th>
                <th>${reportHeader.example}</th>
            </tr>
        """.trimIndent()
    }

    fun createQueryOutputTableHeader(queryHeader: NewQueryOutputParamsHeader): String {
        return """
            <tr>
                <th>${queryHeader.name}</th>
                <th>${queryHeader.values}</th>
                <th>${queryHeader.description}</th>
                <th>${queryHeader.example}</th>
            </tr>
        """.trimIndent()
    }

    fun getReportOutputTableHeader(reportHeader: GetReportOutputParamsHeader): String {
        return """
            <tr>
                <th>${reportHeader.name}</th>
                <th>${reportHeader.values}</th>
                <th>${reportHeader.description}</th>
                <th>${reportHeader.example}</th>
            </tr>
        """.trimIndent()
    }

    fun createQueryInputTableValue(queryItem: B2bSourceCreateNewQueryInputParamsItemDoc): String {
        return """
            <tr>
                <td align="center">${queryItem.name}</td>
                <td align ="center"><b>${if(queryItem.required) "+" else "-"}</b></td>
                <td align="center">${queryItem.values}</td>
                <td align="center">${queryItem.description}</td>
                <td align="center">${queryItem.example}</td>
            </tr>
        """.trimIndent()
    }

    fun getReportInputTableValue(reportItem: B2bSourceCreateGetReportInputParamsItemDoc): String {
        return """
            <tr>
                <td align="center">${reportItem.name}</td>
                <td align ="center"><b>${if(reportItem.required) "+" else "-"}</b></td>
                <td align="center">${reportItem.values}</td>
                <td align="center">${reportItem.description}</td>
                <td align="center">${reportItem.example}</td>
            </tr>
        """.trimIndent()
    }

    fun createQueryOutputTableValue(queryItem: B2bSourceCreateNewQueryOutputParamsItemDoc): String {
        return """
            <tr>
                <td align="center">${queryItem.name}</td>
                <td align="center">${queryItem.values}</td>
                <td align="center">${queryItem.description}</td>
                <td align="center">${queryItem.example}</td>
            </tr>
        """.trimIndent()
    }

    fun getReportOutputTableValue(reportItem: B2bSourceCreateGetReportOutputParamsItemDoc): String {
        return """
            <tr>
                <td align="center">${reportItem.name}</td>
                <td align="center">${reportItem.values}</td>
                <td align="center">${reportItem.description}</td>
                <td align="center">${reportItem.example}</td>
            </tr>
        """.trimIndent()
    }

    fun StringBuilder.setBuilderWithJson(field: Any){
     val result = Json.stringify(field, true)
        val substrings = result.split("/n")
        substrings.forEach {
            this.append(it + "\n")
        }
    }

    val conveyor = konveyor<B2bSourceDocContext> {

        handler {
            exec{
                val header = doc.header
                builder.append("# ${header.prefix}: ${header.name}")
                builder.append("\n")
                builder.append("\n")
                state = B2bDocState.SET_HEADER
            }
        }

        handler {
            exec {
                val newQuery = doc.new_query
                builder.append("## Создание нового запроса")
                builder.append("\n\n")
                builder.append("##### **URL:** ${newQuery.url}")
                builder.append("\n")
                builder.append("##### **Назначение:**  ${newQuery.destination}")
                builder.append("\n")
                builder.append("##### **Тип запроса:** ${newQuery.type}")
                builder.append("\n")
                builder.append("##### **Кодировка:** ${newQuery.encoding}")
                builder.append("\n")

                builder.append("##### **Входные параметры:**")

                //
                builder.append("\n\n")
                builder.append("<table border=1>\n")

                builder.append(createQueryInputTableHeader(newQuery.input.header))

                newQuery.input.input_params.forEach {
                    builder.append(createQueryInputTableValue(it))
                    builder.append("\n")
                }
                builder.append("\n")
                builder.append("</table>\n\n")

                builder.append("##### **Параметры ответа:**")

                //
                builder.append("\n\n")
                builder.append("<table border=1>\n")

                builder.append(createQueryOutputTableHeader(newQuery.output.header))

                newQuery.output.output_params.forEach {
                    builder.append(createQueryOutputTableValue(it))
                    builder.append("\n")
                }
                builder.append("\n")
                builder.append("</table>\n\n")

                builder.append("##### **Пример запроса:**\n")
                builder.append("###### URL: ${newQuery.query_example.url}\n")
                builder.append("###### Method: ${newQuery.query_example.method}\n")
                builder.append("###### Header: ${newQuery.query_example.header}\n")
                builder.append("###### Body:\n\n```json\n")
                builder.setBuilderWithJson(newQuery.query_example.body ?: "")
                builder.append("\n```\n\n")


                builder.append("##### **Пример ответа:**\n\n```json\n")
                builder.setBuilderWithJson(newQuery.response_example.body ?: "")
                builder.append("\n```\n\n")

                builder.append("##### **Пример ответа с ошибкой:**\n\n```json\n")
                builder.setBuilderWithJson(newQuery.response_fail)
                builder.append("\n```\n\n")
                builder.append("----------------------------------------------------\n")
                state = B2bDocState.SET_NEW_QUERY
            }
        }

        handler {
            exec {
                val getReport = doc.get_report
                builder.append("## Получение отчета")
                builder.append("\n\n")
                builder.append("##### **URL:** ${getReport.url}")
                builder.append("\n")
                builder.append("##### **Назначение:**  ${getReport.destination}")
                builder.append("\n")
                builder.append("##### **Тип запроса:** ${getReport.type}")
                builder.append("\n")
                builder.append("##### **Кодировка:** ${getReport.encoding}")
                builder.append("\n")

                builder.append("##### **Входные параметры:**")

                //
                builder.append("\n\n")
                builder.append("<table border=1>\n")

                builder.append(getReportInputTableHeader(getReport.input.header))

                getReport.input.input_params.forEach {
                    builder.append(getReportInputTableValue(it))
                    builder.append("\n")
                }
                builder.append("\n")
                builder.append("</table>\n\n")

                builder.append("##### **Параметры ответа:**")

                //
                builder.append("\n\n")
                builder.append("<table border=1>\n")

                builder.append(getReportOutputTableHeader(getReport.output.header))

                getReport.output.output_params.forEach {
                    builder.append(getReportOutputTableValue(it))
                    builder.append("\n")
                }
                builder.append("\n")
                builder.append("</table>\n\n")

                builder.append("##### **Пример запроса:**\n")
                builder.append("###### URL: ${getReport.query_example.url}\n")
                builder.append("###### Method: ${getReport.query_example.method}\n")
                builder.append("###### Header: ${getReport.query_example.header}\n\n")

                builder.append("##### **Пример ответа:**\n\n```json\n")
                builder.setBuilderWithJson(getReport.response_example)
                builder.append("\n```\n\n")

                state = B2bDocState.SET_REPORT_GET
            }
        }

        handler {
            exec {
                result = builder.toString()
            }
        }
    }

}
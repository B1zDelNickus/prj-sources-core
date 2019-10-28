package codes.spectrum.sources.core.test

import codes.spectrum.sources.DebugMode
import codes.spectrum.sources.SourceQuery
import codes.spectrum.sources.core.source.Case
import codes.spectrum.sources.core.source.SourceDescriptor
import codes.spectrum.sources.core.source.Parameter
import codes.spectrum.sources.core.source.ParameterFactory
import codes.spectrum.sources.core.test.UIGeneratorConstants.INDENT
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_SELF_FORM_PARAM_PREFIX

object GeneratorsExtentions {
    //Список дополнительных параметров запроса
    private val DEBUG_MODE_PARAMS_LIST = listOf("stub", "throwError", "sourceDelay")
    private const val CASE_NAME_PARAM = "name"
    private const val CASE_TIMEOUT_PARAM = "timeout"
    private const val CASE_DESCRIPTION_PARAM = "description"

    fun getDebugParameters(): List<Parameter> =
        ParameterFactory.getParameters(DebugMode::class, DEBUG_MODE_PARAMS_LIST)

    fun getCaseParameters(): List<Parameter> =
        ParameterFactory.getParameters(SourceQuery::class, listOf(CASE_TIMEOUT_PARAM, CASE_DESCRIPTION_PARAM))

    private fun getParameterString(parameter: Parameter, indent: Int = 0, isFromForm: Boolean = false, quotedNames: Boolean = false): String =
        "${INDENT.repeat(indent)}${ if (!quotedNames) parameter.name else "\"${parameter.name}\""}:${ if(isFromForm) "${UI_SELF_FORM_PARAM_PREFIX}${parameter.name}" else "\"${parameter.value ?: ""}\"" }"

    fun getRequestString(caseParams: List<Parameter> = emptyList(),
                         queryParams: List<Parameter> = emptyList(),
                         debugParams: List<Parameter> = emptyList(),
                         indent: Int = 0, isStructed: Boolean = true, isForForm: Boolean = false, quotedNames: Boolean = false): String =
        listOf(
            caseParams
                .joinToString(",\n") { getParameterString(it, indent, isForForm, quotedNames) },
            (if (isStructed) "${INDENT.repeat(indent)}${ if (!quotedNames) "query" else "\"query\""} : {\n" else "") +
                queryParams
                    .joinToString(",\n") { getParameterString(it, if (isStructed) indent + 1 else indent, isForForm, quotedNames) } +
                (if (isStructed) "\n${INDENT.repeat(indent)}}" else ""),
            (if (isStructed) "${INDENT.repeat(indent)}${ if (!quotedNames) "debug" else "\"debug\""} : {\n" else "") +
                debugParams
                    .joinToString(",\n") { getParameterString(it, if (isStructed) indent + 1 else indent, isForForm, quotedNames) } +
                (if (isStructed) "\n${INDENT.repeat(indent)}}" else "")
        ).filter { it != "" }.joinToString(",\n")


    fun getRequestStringForQuery(sourceDescriptor: SourceDescriptor, case: Case, indent: Int = 0, quotedNames: Boolean = false): String =
        getRequestString(
            ParameterFactory.getParameters(case, listOf(CASE_NAME_PARAM, CASE_TIMEOUT_PARAM, CASE_DESCRIPTION_PARAM))
                .map {
                    if (it.name == CASE_DESCRIPTION_PARAM)
                        it.copy(value = sourceDescriptor.caseDescriptionConverter((it.value as String?) ?: ""))
                    else
                        it
                },
            ParameterFactory.getParameters(case.query),
            ParameterFactory.getParameters(case.debug, DEBUG_MODE_PARAMS_LIST),
            indent, isStructed = false, quotedNames = quotedNames
        )

    fun getRequestStringForInsomnia(case: Case, indent: Int = 0, quotedNames: Boolean = false): String =
        getRequestString(
            ParameterFactory.getParameters(case, listOf(CASE_TIMEOUT_PARAM)),
            ParameterFactory.getParameters(case.query),
            ParameterFactory.getParameters(case.debug, DEBUG_MODE_PARAMS_LIST),
            indent, quotedNames = quotedNames
        )
}
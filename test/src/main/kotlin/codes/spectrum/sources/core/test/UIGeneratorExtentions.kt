package codes.spectrum.sources.core.test

import codes.spectrum.sources.core.SourceDefinition
import codes.spectrum.sources.core.source.InputType
import codes.spectrum.sources.core.source.Parameter
import codes.spectrum.sources.core.source.SourceDescriptor
import codes.spectrum.sources.core.test.UIGeneratorConstants.INDENT
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_CASES_AND_SYSTEMS_INDENT
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_CASES_FORM_MARK
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_CASES_LABEL
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_CASES_MARK
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_CASE_INPUT_ID_PREFIX
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_CODES_LIST_MARK
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_DEFAULT_CASE_MARK
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_DEFAULT_RESULT_DATA
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_DOCTYPE
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_DOC_DESCRIPTION_LINK
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_DOC_LINK_PATH
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_DOC_LINK_TEXT
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_FORM_PARAMS_MARK
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_HEAD_DEPENDENCIES
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_INPUT_ID_SUFFIX
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_INSOMNIA_JSON_LINK_PATH
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_INSOMNIA_JSON_TEXT
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_INSOMNIA_LINK_TEXT
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_INSOMNIA_YAML_LINK_PATH
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_INSOMNIA_YAML_TEXT
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_NONE_CASE_MARK
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_QUERY_PARAMS_INDENT
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_QUERY_PARAMS_MARK
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_REACT_CLASSES_MARK
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_REACT_SCRIPT
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_RESULT_DATA_INDENT
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_RESULT_DATA_MARK
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_SOURCE_TITLE_PREFIX
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_SYSTEMS_LABEL
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_SYSTEMS_MARK
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_SYSTEM_INPUT_ID_PREFIX
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_THIS_FORM_PARAM_PREFIX
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_URL_CODE_MARK
import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.Writer

object UIGeneratorExtentions {

    private val DEBUG_MODE_PARAMETERS by lazy { GeneratorsExtentions.getDebugParameters() }
    private val CASE_PARAMETERS by lazy { GeneratorsExtentions.getCaseParameters() }
    private val ADDITIONAL_PARAMS by lazy { DEBUG_MODE_PARAMETERS + CASE_PARAMETERS }

    private fun escapeHTML(text: String = ""): String =
        text
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("&", "&amp;")

    fun tag(name: String, text: String = "", attrs: Map<String, String> = emptyMap(), indent: Int = 0, escapeContent: Boolean = true, emptyContent: Boolean = false, inlineContent: Boolean = true): String {
        val normalName = name.toLowerCase()
        val normalAttrs = attrs.toList()
            .joinToString("") {
                if (it.second[0] == '{')
                    """ ${it.first}=${it.second}"""
                else
                    """ ${it.first}="${it.second}""""
            }
        val mainIndent = INDENT.repeat(indent)
        val bodyIndent = INDENT.repeat(indent + 1)

        if (text.isEmpty() && !emptyContent)
            return "$mainIndent<$normalName$normalAttrs/>\n"

        val normalText = if (escapeContent) escapeHTML(text) else text

        val content = "$bodyIndent$normalText".replace("\n$".toRegex(), "").split("\n").joinToString("\n$bodyIndent")
        return if (inlineContent)
            "$mainIndent<$normalName$normalAttrs>$normalText</$normalName>"
        else
            "$mainIndent<$normalName$normalAttrs>\n$content\n$mainIndent</$normalName>"
    }

    fun Writer.tag(name: String, attrs: Map<String, String> = emptyMap(), indent: Int = 0, body: (Int) -> Unit) {
        val normalName = name.toLowerCase()
        val normalAttrs = attrs.toList()
            .joinToString("") {
                if (it.second[0] == '{')
                    """ ${it.first}=${it.second}"""
                else
                    """ ${it.first}="${it.second}""""
            }
        val mainIndent = INDENT.repeat(indent)

        appendln("$mainIndent<$normalName$normalAttrs>")
        body(indent + 1)
        appendln("$mainIndent</$normalName>")
    }


    fun Writer.h1(text: String, indent: Int = 0, escapeContent: Boolean = true) {
        appendln(tag("h1", text, indent = indent, escapeContent = escapeContent))
    }

    fun Writer.p(text: String, indent: Int = 0, escapeContent: Boolean = true) {
        appendln(tag("p", text, indent = indent, escapeContent = escapeContent))
    }

    fun Writer.rowDiv(indent: Int = 0, attrs: Map<String, String> = emptyMap(), body: (Int) -> Unit = {}) {
        tag("div", mapOf("class" to "row") + attrs, indent, body)
    }

    fun link(prefixString: String = "", linkText: String = "", link: String = "", postfixString: String = ""): String =
        "$prefixString${tag("a", linkText, mapOf("href" to link))}$postfixString"

    fun Writer.sourceTitle(indent: Int = 0) {
        h1("$UI_SOURCE_TITLE_PREFIX${SourceDefinition.Instance.name} (${SourceDefinition.Instance.code})", indent)
    }

    fun Writer.insomniaLink(text: String = UI_INSOMNIA_LINK_TEXT,
                            links: Map<String, String> = mapOf(
                                UI_INSOMNIA_JSON_TEXT to UI_INSOMNIA_JSON_LINK_PATH,
                                UI_INSOMNIA_YAML_TEXT to UI_INSOMNIA_YAML_LINK_PATH),
                            indent: Int = 0) {
        val content = links.toList().joinToString(", ", prefix = "$text (", postfix = ")<br>") { link(linkText = it.first, link = it.second) }
        p(content, indent = indent, escapeContent = false)
    }

    fun Writer.docLink(prefixString: String = UI_DOC_DESCRIPTION_LINK, linkText: String = UI_DOC_LINK_TEXT, link: String = UI_DOC_LINK_PATH, postfixString: String = "", indent: Int = 0) {
        p(link("$prefixString ", linkText, link, postfixString), indent = indent, escapeContent = false)
    }

    fun Writer.writeDoc(body: (Int) -> Unit) {
        appendln(UI_DOCTYPE)
        tag("html", mapOf("lang" to "en"), 0, body)
    }

    fun Writer.writeHead(indent: Int = 0, body: (Int) -> Unit) {
        tag("head", indent = indent) {
            appendln(UI_HEAD_DEPENDENCIES)
            body(indent + 1)
        }
    }

    fun Writer.writeBody(indent: Int = 0, body: (Int) -> Unit) {
        tag("body", mapOf("class" to "container col align-items-center"), indent, body)
    }
    
    private fun getFormParamsString(sourceDescriptor: SourceDescriptor): String =
        (sourceDescriptor.queryList + ADDITIONAL_PARAMS)
            .joinToString(" ") { "${it.name}:\"\"," }

    private fun getCasesString(sourceDescriptor: SourceDescriptor, indent: Int = 0): String =
        sourceDescriptor.uiCases
            .joinToString(",\n") { case ->
                "${INDENT.repeat(indent)}${sourceDescriptor.getCodeByCase(case)} : {\n" +
                    GeneratorsExtentions.getRequestStringForQuery(sourceDescriptor, case, indent + 1) +
                    "\n${INDENT.repeat(indent)}}"
            }

    private fun getRequestStringForForm(sourceDescriptor: SourceDescriptor, indent: Int = 0, quotedNames: Boolean = false): String =
        GeneratorsExtentions.getRequestString(CASE_PARAMETERS, sourceDescriptor.queryList, DEBUG_MODE_PARAMETERS, indent, isForForm = true, quotedNames = quotedNames)

    private fun getSystemsString(sourceDescriptor: SourceDescriptor, indent: Int = 0): String =
        sourceDescriptor.restSystems
            .joinToString(",\n") { system ->
                "${INDENT.repeat(indent)}${sourceDescriptor.getCodeByRestSystem(system)} : {\n" +
                    "${INDENT.repeat(indent + 1)}name:\"${system.name}\",\n" +
                    "${INDENT.repeat(indent + 1)}url:\"${system.url}\"" +
                    "\n${INDENT.repeat(indent)}}"
            }

    private fun getListedInput(code: String, labelContent: String, items: List<Pair<String, String>>, inputValue: String = "", isFormParam: Boolean = true): String {
        val id = "$code${UI_INPUT_ID_SUFFIX}"
        var options = ""
        items.forEach {
            val value = it.first
            val key = it.second
            options += tag("option", value, mapOf("key" to key, "value" to key), 0, false)
            options += "\n"
        }
        val value = if (isFormParam) "{${UI_THIS_FORM_PARAM_PREFIX}$code}"
            else inputValue
        val select = tag("select", options, mapOf("id" to id, "name" to code, "className" to "form-control", "value" to value, "onChange" to "{this.handleChange}"), 0, false, inlineContent = false)
        val span = tag("span", labelContent, mapOf("title" to code), 0)
        val label = tag("label", span, mapOf("htmlFor" to id), 0, false)
        return tag("div", "$label\n$select", mapOf("className" to "form-group", "key" to code), 6, false, inlineContent = false)
    }

    private fun getInput(code: String, labelContent: String, inputType: String = "text", inputValue: String = "", isFormParam: Boolean = true): String {
        val id = "$code${UI_INPUT_ID_SUFFIX}"
        val span = tag("span", labelContent, mapOf("title" to code), 0)
        val label = tag("label", span, mapOf("htmlFor" to id), 0, false)
        val value = if (isFormParam) "{${UI_THIS_FORM_PARAM_PREFIX}$code}"
            else inputValue
        val attrsMap = mutableMapOf("id" to id, "className" to "form-control", "type" to inputType, "name" to code, "value" to value, "onChange" to "{this.handleChange}")
        if (inputType == "checkbox")
            attrsMap["defaultChecked"] = "${value.replace("}$".toRegex(), "")}=='true'}"
        val input = tag("input", "", attrsMap)
        return tag("div", "$label\n$input", mapOf("className" to "form-group", "key" to code), 6, false, inlineContent = false)
    }

    private fun getInput(parameter: Parameter): String {
        val code = parameter.name
        if (parameter.inputType == InputType.ENUM) {
            val values = parameter.clazz?.fields?.map { field ->
                val name = field?.getAnnotation(SerializedName::class.java)?.value ?: field.name
                Pair(name, name)
            } ?: emptyList()
            return getListedInput(code, parameter.title, values)
        }
        return getInput(code, parameter.title, parameter.normalizedType)
    }    
    
    private fun getInputsString(sourceDescriptor: SourceDescriptor): String {
        val casesPairs = sourceDescriptor.uiCasesPairs
        val systemsPairs = sourceDescriptor.restSystemsPairs
        val inputStrings = listOf(
            getListedInput(UI_CASE_INPUT_ID_PREFIX, UI_CASES_LABEL, casesPairs),
            getListedInput(UI_SYSTEM_INPUT_ID_PREFIX, UI_SYSTEMS_LABEL, systemsPairs)
        )
        return (
            inputStrings +
                sourceDescriptor.queryList.map { getInput(it) } +
                ADDITIONAL_PARAMS.map { getInput(it) }
            ).joinToString("\n")
    }

    fun reactScript(
        sourceDescriptor: SourceDescriptor,
        indent: Int): String {
        val reactClasses = File("src/test/resources/includes/AdditionalReactClasses.js")
        val result = File("src/test/resources/includes/Result.txt")

        val additionalReactClasses = if (reactClasses.exists()) reactClasses.readText() else ""

        val normalizedResult =
            (if (result.exists()) result.readText() else "")
                .let { if (it.isBlank()) UI_DEFAULT_RESULT_DATA else it }
                .lines()
                .joinToString("\n") { "${INDENT.repeat(UI_RESULT_DATA_INDENT)}$it" }

        return tag("script",
            UI_REACT_SCRIPT
                .replace(UI_FORM_PARAMS_MARK, getFormParamsString(sourceDescriptor))
                .replace(UI_QUERY_PARAMS_MARK, getRequestStringForForm(sourceDescriptor, UI_QUERY_PARAMS_INDENT))
                .replace(UI_URL_CODE_MARK, SourceDefinition.Instance.code)
                .replace(UI_CASES_MARK, getCasesString(sourceDescriptor, UI_CASES_AND_SYSTEMS_INDENT))
                .replace(UI_SYSTEMS_MARK, getSystemsString(sourceDescriptor, UI_CASES_AND_SYSTEMS_INDENT))
                .replace(UI_CASES_FORM_MARK, getInputsString(sourceDescriptor))
                .replace(UI_DEFAULT_CASE_MARK, sourceDescriptor.defaultCaseCode)
                .replace(UI_NONE_CASE_MARK, SourceDescriptor.NONE_CASE_CODE)
                .replace(UI_REACT_CLASSES_MARK, additionalReactClasses)
                .replace(UI_RESULT_DATA_MARK, normalizedResult)
                .replace(UI_CODES_LIST_MARK, sourceDescriptor.goodCodes.joinToString(", ", "[", "]") { "\"${it.name}\"" }),
            mapOf("type" to "text/babel"), indent, false)
    }
}
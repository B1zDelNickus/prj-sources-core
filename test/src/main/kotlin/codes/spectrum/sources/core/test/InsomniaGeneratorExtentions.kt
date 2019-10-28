package codes.spectrum.sources.core.test

import codes.spectrum.sources.core.SourceDefinition
import codes.spectrum.sources.core.source.SourceDescriptor
import codes.spectrum.sources.core.source.StructureObject
import codes.spectrum.sources.core.source.StructureParam
import codes.spectrum.sources.core.test.UIGeneratorConstants.INDENT
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_URL
import codes.spectrum.sources.core.test.UIGeneratorConstants.UI_URL_CODE_MARK
import org.joda.time.Instant

object InsomniaGeneratorExtentions {

    private fun getIdString(prefix: String, postfix: String = "", isCodeNeeded: Boolean = false): String =
        "${prefix}_${if (isCodeNeeded) SourceDefinition.Instance.code else ""}$postfix"

    private fun getWorkSpaceId(postfix: String = ""): String =
        getIdString(prefix = "wrk", postfix = postfix, isCodeNeeded = true)

    private fun getFolderId(postfix: String): String =
        getIdString(prefix = "fld", postfix = postfix)

    private fun getRequestId(caseCode: String, systemCode: String): String =
        getIdString(prefix = "req", postfix = "$caseCode$systemCode")

    private fun getPairId(postfix: String): String =
        getIdString(prefix = "pair", postfix = postfix)

    private fun getWorkSpaceJsonObject(sourceDescriptor: SourceDescriptor, name: String, id: String, millis: Long): StructureObject =
        StructureObject(
            params = listOf(
                StructureParam("_id", id),
                StructureParam("created", millis.toString(), needQuotes = false),
                StructureParam("description", sourceDescriptor.description),
                StructureParam("modified", millis.toString(), needQuotes = false),
                StructureParam("name", name),
                StructureParam("parentId"),
                StructureParam("_type", "workspace")
            )
        )

    private fun getFoldersJsonObjects(sourceDescriptor: SourceDescriptor, parentId: String, millis: Long): List<StructureObject> =
        sourceDescriptor.restSystems
            .map { system ->
                StructureObject(
                    params = listOf(
                        StructureParam("_id", getFolderId(sourceDescriptor.getCodeByRestSystem(system))),
                        StructureParam("created", millis.toString(), needQuotes = false),
                        StructureParam("metaSortKey", (-millis).toString(), needQuotes = false),
                        StructureParam("modified", millis.toString(), needQuotes = false),
                        StructureParam("name", system.name),
                        StructureParam("parentId", parentId),
                        StructureParam("_type", "request_group")
                    ))
            }

    private fun getRequestsObjects(sourceDescriptor: SourceDescriptor, millis: Long): List<StructureObject> =
        sourceDescriptor.restSystems
            .flatMap { system ->
                sourceDescriptor.uiCases
                    .map { case ->
                        StructureObject(
                            params = listOf(
                                StructureParam("_id", getRequestId(sourceDescriptor.getCodeByCase(case), sourceDescriptor.getCodeByRestSystem(system))),
                                StructureParam("created", millis.toString(), needQuotes = false),
                                StructureParam("description", case.description),
                                StructureParam("metaSortKey", (-millis).toString(), needQuotes = false),
                                StructureParam("modified", millis.toString(), needQuotes = false),
                                StructureParam("method", "POST"),
                                StructureParam("parentId", getFolderId(sourceDescriptor.getCodeByRestSystem(system))),
                                StructureParam("url", UI_URL.replace("\${url}", system.url.replace("/$".toRegex(), "")).replace(UI_URL_CODE_MARK, SourceDefinition.Instance.code)),
                                StructureParam("name", case.name),
                                StructureParam("_type", "request")
                            ),
                            objects = listOf(
                                Pair("body", StructureObject(
                                    params = listOf(
                                        StructureParam("mimeType", "application/json;charset=utf-8"),
                                        StructureParam("text", "{\n${INDENT}\"caseCode\":\"${sourceDescriptor.getCodeByCase(case)}\",\n${GeneratorsExtentions.getRequestStringForInsomnia(case, 1, true)}\n}")
                                    ))
                                )
                            ),
                            arrays = listOf(
                                Pair("headers",
                                    listOf(
                                        StructureObject(
                                            params = listOf(
                                                StructureParam("id", getPairId("Content-Type")),
                                                StructureParam("name", "Content-Type"),
                                                StructureParam("value", "application/json;charset=utf-8"))
                                        )
                                    )
                                )
                            )
                        )
                    }
            }

    fun getInsomniaObject(sourceDescriptor: SourceDescriptor, time: Instant, workSpaceName: String = "", workSpaceId: String = ""): StructureObject {
        val date = time.toString()
        val millis = time.millis
        val wrkId = if (workSpaceId.isBlank()) getWorkSpaceId() else getWorkSpaceId(workSpaceId)
        val wrkName = if (workSpaceName.isBlank()) SourceDefinition.Instance.name else workSpaceName
        return StructureObject(
            params = listOf(
                StructureParam("_type", "export"),
                StructureParam("__export_format", "4", needQuotes = false),
                StructureParam("__export_date", date),
                StructureParam("__export_source", "insomnia.desktop.app:v6.6.2")
            ),
            arrays = listOf(
                Pair(
                    "resources",
                    listOf(getWorkSpaceJsonObject(sourceDescriptor, wrkName, wrkId, millis))
                        + getFoldersJsonObjects(sourceDescriptor, wrkId, millis)
                        + getRequestsObjects(sourceDescriptor, millis)
                )
            )
        )
    }
}
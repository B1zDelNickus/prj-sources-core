package codes.spectrum.sources.core.client.b2bdocutils

import codes.spectrum.utils.common.FileUtils
import java.nio.charset.Charset

data class B2bSourceDoc(
        var file_name: String = "",
        var header: B2bSourceHeaderDoc = B2bSourceHeaderDoc(),
        var new_query: B2bSourceCreateNewQueryDoc = B2bSourceCreateNewQueryDoc(),
        var get_report: B2bSourceCreateGetReportDoc = B2bSourceCreateGetReportDoc()
) {

    fun createMd(){
        FileUtils.writeFile(file_name, B2bSourceDocCreator.create(this).toByteArray(Charset.forName("utf-8")))
    }

    fun createSimpleJson(){
        var simpleFileName = file_name
        if(simpleFileName.endsWith(".md")){
            simpleFileName = simpleFileName.split(".md")[0] + ".json"
        }

        FileUtils.writeFile(simpleFileName, B2bSourceDocCreator.createSimpleJson(this).toByteArray(Charset.forName("utf-8")))
    }

    operator fun plus(fileName: String){
        this.file_name = fileName
    }
}
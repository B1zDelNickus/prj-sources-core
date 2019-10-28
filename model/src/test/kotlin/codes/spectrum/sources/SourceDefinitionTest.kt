package codes.spectrum.sources

import codes.spectrum.serialization.json.Json
import codes.spectrum.sources.config.EnvProxy
import codes.spectrum.sources.core.SourceDefinition
import io.kotlintest.TestCase
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.File

class SourceDefinitionTest : StringSpec({
    "can load by default"{
        SourceDefinition.Instance.code shouldBe "core"
    }
    "can load from env defined file"{
        val tmpFile = File("../build/tmp/test_json.json")
        tmpFile.parentFile!!.mkdirs()
        val sampleSource = SourceDefinition(code="test", name = "best", librarySet = false)
        tmpFile.writeText(Json.stringify(sampleSource))
        config.set(SourceDefinition.SourceDefinitionFileEnvVariable,tmpFile.canonicalPath)
        val resultDefinition = SourceDefinition.load(config)
        resultDefinition shouldBe sampleSource
    }
    "can load from env defined json"{
        val sampleSource = SourceDefinition(code="test", name = "best", librarySet = false)
        config.set(SourceDefinition.SourceDefinitionJsonEnvVariable, Json.stringify(sampleSource))
        val resultDefinition = SourceDefinition.load(config)
        resultDefinition shouldBe sampleSource
    }
}){
    override fun beforeTest(testCase: TestCase) {
        config.clear()
    }
    companion object {
        val config = EnvProxy()
    }
}
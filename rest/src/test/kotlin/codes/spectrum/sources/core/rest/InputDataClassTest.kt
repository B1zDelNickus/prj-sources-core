package codes.spectrum.sources.core.rest

import codes.spectrum.sources.SourceQuery
import codes.spectrum.sources.json.jget
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.runBlocking

class SourceQueryDataExample(var year: Int = 0)
class SourceQueryExample : SourceQuery<SourceQueryDataExample>(SourceQueryDataExample())

class InputDataClassTest : StringSpec({
    "simple text body"{
        runBlocking {
            val json = InputDataParser.Instance.parseInput(HashMap::class.java, { """{"x":1}""" })
            json.jget("x") shouldBe 1.0
        }
    }
    "simple text body and override"{
        runBlocking {
            val json = InputDataParser.Instance.parseInput(
                HashMap::class.java,
                { """{"x":1}""" },
                { mapOf("x" to 2) }
            )
            json.jget("x") shouldBe 2.0
        }
    }

    "complex text body and override"{
        runBlocking {
            val json = InputDataParser.Instance.parseInput(
                HashMap::class.java,
                { """{"x":{"z":3,"u":2}}""" },
                { mapOf("x.z" to 4) }
            )
            json.jget("x.u") shouldBe 2.0
            json.jget("x.z") shouldBe 4.0
        }
    }

    "complex text body and defaultPrefix"{
        runBlocking {
            val json = InputDataParser.Instance.parseInput(
                HashMap::class.java,
                { """{"x":{"z":3,"u":2}}""" },
                { mapOf("nnn" to 4) },
                "x"
            )
            json.jget("nnn") shouldBe 4.0
            json.jget("x.nnn") shouldBe 4.0
        }
    }


    "complex text body and defaultPrefix soft and override"{
        runBlocking {
            val json = InputDataParser.Instance.parseInput(
                HashMap::class.java,
                { """{"x":{"z":3,"u":2}}""" },
                { mapOf("z" to 4, "x.u" to 3) },
                "x"
            )
            json.jget("z") shouldBe 4.0
            json.jget("x.z") shouldBe 3.0
            json.jget("x.u") shouldBe 3.0
        }
    }

    "can get SourceQuery from get parameters without dots"{
        runBlocking {
            val query = InputDataParser.Instance.parseInput(
                SourceQueryExample::class.java,
                { """{}""" },
                { mapOf("year" to 2017) },
                "query"
            )
            query.query.year shouldBe 2017
        }
    }
})
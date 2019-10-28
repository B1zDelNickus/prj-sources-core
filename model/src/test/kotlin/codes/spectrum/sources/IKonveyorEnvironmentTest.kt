package codes.spectrum.sources

import codes.spectrum.konveyor.konveyor
import codes.spectrum.sources.config.EnvProxy
import codes.spectrum.sources.config.IConfig
import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.newFixedThreadPoolContext


class IKonveyorEnvironmentTest : StringSpec({
    "property accessibility" {
        val env = EnvProxy.Instance

        env.set("testProp", "testPropValue")

        assertSoftly {
            env.has("testProp") shouldBe true
            env.has("testProp", String::class) shouldBe true
            env.has("testProp", Int::class) shouldBe false
            env.has("missingProp") shouldBe false
            env.get<String>("testProp", String::class) shouldBe "testPropValue"
        }
    }

    "complex property" {
        val env = EnvProxy.Instance

        val property = newFixedThreadPoolContext(32, "asyncSave")

        env.set("property", property)

        env.get<ExecutorCoroutineDispatcher>("property", ExecutorCoroutineDispatcher::class) shouldBe property
    }

    "context test" {
        val konveyor = konveyor<SourceContext<String, String>> {
            execEnv {
                incrementSuccess(it as IConfig)
                addError(Exception("test error"), it)
            }
        }

        val context = SourceContext(SourceQuery(query = "query"), SourceResult(data = "data"))

        konveyor.exec(context, EnvProxy.Instance)

        context.statistics.error.get() shouldBe 1
        context.statistics.successful.get() shouldBe 1
        context.statistics.total.get() shouldBe 2
        context.getErrors(EnvProxy.Instance).first().message shouldBe "test error"
    }
})
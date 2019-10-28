package codes.spectrum.sources.core.test

import codes.spectrum.sources.core.SourceDefinition
import codes.spectrum.sources.core.source.Case
import codes.spectrum.sources.core.source.SourceDescriptor
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.runBlocking

open class TestCasesTestBase(
    sourceDescriptor: SourceDescriptor,
    body: TestCasesTestBase.() -> Unit = {}
) : StringSpec({
    //Тестирование кейсов
    sourceDescriptor.testCases.forEach { case: Case ->
        "${SourceDefinition.Instance.code} проверка кейса ${case.name}" {
            val provider = sourceDescriptor.createSource()
            val request =
                sourceDescriptor.createRequest(
                    case.query ?: sourceDescriptor.createQuery(),
                    case.debug,
                    sourceDescriptor.getCodeByCase(case)
                ).apply { timeout = case.timeout }
            val context = sourceDescriptor.createContext(request)

            if (case.execute != null)
                case.execute!!.invoke(provider, context)
            else
                runBlocking { provider.execute(context) }

            val result = context.result
            val check = case.validate(result)
            check.isOk shouldBe true
        }
    }

    (this as TestCasesTestBase).body()
})
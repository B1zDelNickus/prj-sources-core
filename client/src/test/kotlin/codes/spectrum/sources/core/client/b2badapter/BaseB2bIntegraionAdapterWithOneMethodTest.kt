package codes.spectrum.sources.core.client.b2badapter

import codes.spectrum.data.SourceDescriptor
import codes.spectrum.data.SourceDescriptorFactory
import codes.spectrum.message.Message
import codes.spectrum.sources.ISourceHandler
import codes.spectrum.sources.SourceContext
import codes.spectrum.sources.SourceQuery
import codes.spectrum.sources.SourceResult
import com.nhaarman.mockitokotlin2.mock
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BaseB2bIntegraionAdapterWithOneMethodTest: StringSpec({

    val client = mock<ISourceHandler<SourceContext<TestQuery, TestResult>>>{}
    val descriptor = SourceDescriptor(code = "check_person/egrul")
    val adapter by lazy { TestIntegrationWithOneMethodAdapter(client, descriptor = descriptor) }

    "field resultFromDataClazz return TestResult::class.java"{
        adapter.resultFromDataClazz shouldBe TestResult::class.java
    }

    "method resultFromData return TestResult"{
        adapter.resultFromData(TestResult("answer")) shouldBe TestResult("answer")
        adapter.resultFromData(TestQuery()) shouldBe TestResult()
    }

    "method resultFromData return null"{
        adapter.resultFromData(null) shouldBe null
        adapter.resultFromData("") shouldBe null
    }

})

class TestIntegrationWithOneMethodAdapter(
        client: ISourceHandler<SourceContext<TestQuery, TestResult>>,
        logger: Logger = LoggerFactory.getLogger(TestIntegrationAdapter::class.java),
        descriptor: SourceDescriptor = SourceDescriptorFactory.get("check_person/egrul")
): BaseB2bIntegraionAdapterWithOneMethod<TestQuery, TestResult>(client, logger, descriptor) {

    override fun getContext(message: Message): SourceContext<TestQuery, TestResult> {
        return SourceContext(query = SourceQuery(TestQuery()), result = SourceResult(data = TestResult()))
    }

    override val resultFromDataClazz get() = TestResult::class.java
}
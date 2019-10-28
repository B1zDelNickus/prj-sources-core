package codes.spectrum.sources.core.client.b2badapter

import codes.spectrum.message.Message
import codes.spectrum.source.SourceResultStatus
import codes.spectrum.sources.ISourceHandler
import codes.spectrum.sources.SourceContext
import codes.spectrum.sources.SourceQuery
import codes.spectrum.sources.SourceResult
import codes.spectrum.api.SourceState
import codes.spectrum.data.SourceDescriptor
import codes.spectrum.data.SourceDescriptorFactory
import codes.spectrum.serialization.json.Json
import codes.spectrum.source.exception.ExecutionException
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class BaseB2bIntegrationAdapterTest: StringSpec( {

    "source not supported" {
        val notSupportedClient = mock<ISourceHandler<SourceContext<TestQuery, TestResult>>>{}

        val exception = shouldThrow<Throwable> {
            TestIntegrationAdapter(notSupportedClient, descriptor = SourceDescriptorFactory.get("test"))
        }

        exception.message shouldBe "Source with name : test not supported source-app!"
    }

    "exceeded the number of attempts" {
        val exceededClient = mock<ISourceHandler<SourceContext<TestQuery, TestResult>>> {}

        val source = TestIntegrationAdapter(exceededClient)
        val actualResult = source.execute(message(21))

        actualResult.data shouldBe null
        actualResult.pendingPayload shouldBe null
        actualResult.pendingTimeout shouldBe null
        actualResult.error shouldNotBe null
        actualResult.error?.message shouldBe "Exceeded the number of attempts. (21 from 20)"
        actualResult.state() shouldBe SourceResultStatus.Error
        actualResult.stateV2() shouldBe SourceState.ABORTED
    }

    "progress case handled" {
        val progressClient = mock<ISourceHandler<SourceContext<TestQuery, TestResult>>> {
            onBlocking {
                execute(any(), any())
            } doAnswer {
                val context = it.getArgument<SourceContext<TestQuery, TestResult>>(0)
                context.result.status = SourceState.PROGRESS
                Unit
            }
        }

        val message = message()

        val source = TestIntegrationAdapter(progressClient, descriptor = SourceDescriptor(code = "check_person/egrul", timeoutForProgress = 300_000L))
        val actualResult = source.execute(message)

        actualResult.data shouldBe TestResult()
        actualResult.pendingPayload shouldBe message.header.uid
        actualResult.pendingTimeoutForProgress shouldBe 300_000L
        actualResult.error shouldBe null
        actualResult.state() shouldBe SourceResultStatus.Pending
        actualResult.stateV2() shouldBe SourceState.PROGRESS
    }

    "delay case handled" {
        val delayClient = mock<ISourceHandler<SourceContext<TestQuery, TestResult>>> {
            onBlocking {
                execute(any(), any())
            } doAnswer {
                val context = it.getArgument<SourceContext<TestQuery, TestResult>>(0)
                context.result.status = SourceState.RECOVERABLE_CLIENT_ERROR
                Unit
            }
        }

        val message = message()

        val source = TestIntegrationAdapter(delayClient, descriptor = SourceDescriptor(code = "check_person/egrul", timeout = 50_000L))
        val actualResult = source.execute(message)

        actualResult.data shouldBe TestResult()
        actualResult.pendingPayload shouldBe message.header.uid
        actualResult.pendingTimeout shouldBe 50_000L
        actualResult.error shouldBe null
        actualResult.state() shouldBe SourceResultStatus.Pending
        actualResult.stateV2() shouldBe SourceState.RECOVERABLE_CLIENT_ERROR
    }

    "ok case handled" {

        val okClient = mock<ISourceHandler<SourceContext<TestQuery, TestResult>>> {
            onBlocking {
                execute(any(), any())
            } doAnswer {
                val context = it.getArgument<SourceContext<TestQuery, TestResult>>(0)
                context.result = SourceResult(data = TestResult("answer"))
                context.result.status = SourceState.OK
                Unit
            }
        }

        val source = TestIntegrationAdapter(okClient, descriptor = SourceDescriptor(code = "check_person/egrul"))
        val actualResult = source.execute(message())

        actualResult.data shouldBe TestResult("answer")
        actualResult.pendingPayload shouldBe null
        actualResult.pendingTimeout shouldBe null
        actualResult.error shouldBe null
        actualResult.state() shouldBe SourceResultStatus.Ok
        actualResult.stateV2() shouldBe SourceState.OK
    }

    "not found (good) case handled" {

        val notFoundClient = mock<ISourceHandler<SourceContext<TestQuery, TestResult>>> {
            onBlocking {
                execute(any(), any())
            } doAnswer {
                val context = it.getArgument<SourceContext<TestQuery, TestResult>>(0)
                context.result = SourceResult(data = TestResult())
                context.result.status = SourceState.NOT_FOUND
                Unit
            }
        }

        val source = TestIntegrationAdapter(notFoundClient, descriptor = SourceDescriptor(code = "check_person/egrul"))
        val actualResult = source.execute(message())

        actualResult.data shouldBe TestResult()
        actualResult.pendingPayload shouldBe null
        actualResult.pendingTimeout shouldBe null
        actualResult.error shouldBe null
        actualResult.state() shouldBe SourceResultStatus.Ok
        actualResult.stateV2() shouldBe SourceState.NOT_FOUND
    }

    "badQuery case handled" {

        val badQueryClient = mock<ISourceHandler<SourceContext<TestQuery, TestResult>>> {
            onBlocking {
                execute(any(), any())
            } doAnswer {
                val context = it.getArgument<SourceContext<TestQuery, TestResult>>(0)
                context.result = SourceResult(data = TestResult())
                context.result.status = SourceState.BAD_QUERY
                Unit
            }
        }

        val source = TestIntegrationAdapter(badQueryClient, descriptor = SourceDescriptor(code = "check_person/egrul"))
        val actualResult = source.execute(message())

        actualResult.data shouldBe TestResult()
        actualResult.pendingPayload shouldBe null
        actualResult.pendingTimeout shouldBe null
        actualResult.error shouldNotBe null
        actualResult.state() shouldBe SourceResultStatus.Error
        actualResult.stateV2() shouldBe SourceState.BAD_QUERY
    }

    "error case handled" {
        val errorClient = mock<ISourceHandler<SourceContext<TestQuery, TestResult>>> {
            onBlocking {
                execute(any(), any())
            } doAnswer {
                val context = it.getArgument<SourceContext<TestQuery, TestResult>>(0)
                context.result.status = SourceState.SERVICE_ERROR
                context.result.error = NullPointerException("NPE")
                Unit
            }
        }

        val source = TestIntegrationAdapter(errorClient, descriptor = SourceDescriptor(code = "check_person/egrul"))
        val actualResult = source.execute(message())

        actualResult.data shouldBe TestResult()
        actualResult.pendingPayload shouldBe null
        actualResult.pendingTimeout shouldBe null
        actualResult.error shouldNotBe null
        actualResult.error?.message shouldBe "NPE"
        actualResult.state() shouldBe SourceResultStatus.Error
        actualResult.stateV2() shouldBe SourceState.SERVICE_ERROR
    }

    "integration adapter inner error" {
        val innerErrorClient = mock<ISourceHandler<SourceContext<TestQuery, TestResult>>> {
            onBlocking {
                execute(any(), any())
            } doAnswer {
                throw ExecutionException("inner exception")
            }
        }

        val source = TestIntegrationAdapter(innerErrorClient, descriptor = SourceDescriptor(code = "check_person/egrul"))
        val actualResult = source.execute(message())

        actualResult.data shouldBe null
        actualResult.pendingPayload shouldBe null
        actualResult.pendingTimeout shouldBe null
        actualResult.error shouldNotBe null
        actualResult.error?.message shouldBe "inner exception"
        actualResult.state() shouldBe SourceResultStatus.Error
        actualResult.stateV2() shouldBe SourceState.INTEGRATION_ERROR
    }

    "not exist field - message" {
        val notExistFieldClient = mock<ISourceHandler<SourceContext<TestQuery, TestResult>>>{}

        val source = TestIntegrationAdapter(notExistFieldClient, descriptor = SourceDescriptor(code = "check_person/egrul"))

        val exception = shouldThrow<Exception> {
            source.createResultFromContext(SourceContext(SourceQuery(TestQuery()), SourceResult(data = TestResult())))
        }

        exception.message shouldBe "Field with name \"message\" in options doesn't exist!"

    }

})

class TestIntegrationAdapter(
        client: ISourceHandler<SourceContext<TestQuery, TestResult>>,
        logger: Logger = LoggerFactory.getLogger(TestIntegrationAdapter::class.java),
        descriptor: SourceDescriptor = SourceDescriptorFactory.get("check_person/egrul")
): BaseB2bIntegrationAdapter<TestQuery, TestResult>(client, logger, descriptor) {

    override fun getContext(message: Message): SourceContext<TestQuery, TestResult> {
        return SourceContext(query = SourceQuery(TestQuery()), result = SourceResult(data = TestResult()))
    }

    override fun isApplicable(code: String): Boolean {
        return true
    }

    override fun resultFromData(data: Any?): TestResult? {
        return if(data != null) {
            Json.read(Json.jsonify(data), TestResult::class.java)
        } else {
            null
        }
    }
}

fun  message(countTriesLimit: Int = 0) = Message.fromJson("""
    {
  "header": {
    "srcuid": "",
    "task": {
      "name": "",
      "data": [
        {}
      ],
      "options": {
        "request": {
          "sources": [
            "check_person/egrul"
          ]
        },
        "query": {
            "count_tries_limit" : "$countTriesLimit"
        }
      }
    }
  },
  "body": {
    "size": 1,
    "data": [
      {}
    ],
    "options": {}
  }
}
""".trimIndent())

data class TestQuery(
        var query: String = "test_query"
)

data class TestResult(
        var answer: String = "test_answer"
)
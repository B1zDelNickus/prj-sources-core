package codes.spectrum.sources.config

import io.kotlintest.TestCase
import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class ConfigWrapperTest : StringSpec ({
    "proxy params available through wrapper" {
        proxy.set(nonExistentKey, defaultValue)
        wrapper = ConfigWrapper(proxy)
        wrapper.get(nonExistentKey) shouldBe defaultValue
    }
    "wrapper params available" {
        wrapper = ConfigWrapper(proxy) { put(nonExistentKey, defaultValue) }
        wrapper.get(nonExistentKey) shouldBe defaultValue
    }
    "wrapper params shadow proxy params if same name" {
        proxy.set(nonExistentKey, "123")
        wrapper = ConfigWrapper(proxy) {
            put(nonExistentKey, "12345")
        }
        wrapper.get(nonExistentKey) shouldBe "12345"
    }
    "wrapper params are annotated" {
        wrapper = ConfigWrapper(proxy) {
            put(nonExistentKey, defaultValue)
        }
        val prop = wrapper.getAnnotated(nonExistentKey)
        assertSoftly {
            prop.value shouldBe defaultValue
            prop.requestedName shouldBe nonExistentKey
            prop.internalName shouldBe nonExistentKey
            prop.evidence shouldBe wrapper.evidence
            prop.defined shouldBe true
            prop.overriden shouldBe true
            prop.sourceInstance shouldBe wrapper
        }
    }
}) {
    override fun beforeTest(testCase: TestCase) {
        proxy.clear()
    }
    
    companion object {
        const val nonExistentKey = "NON_EXISTENT"
        const val defaultValue = "DEFAULT VALUE"
        val proxy = EnvProxy()
        lateinit var wrapper: ConfigWrapper
    }
}
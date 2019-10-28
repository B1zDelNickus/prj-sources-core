package codes.spectrum.sources.config

import io.kotlintest.TestCase
import io.kotlintest.matchers.types.shouldBeSameInstanceAs
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

class EnvProxyTest : StringSpec({
    "can get propery"{
        val proxy_path = proxy.get(sysKey)
        proxy_path shouldBe sysValue
    }
    "can override propery"{
        proxy.set(sysKey, sysValue + "_test")
        val proxy_path = proxy.get(sysKey)
        proxy_path shouldBe sysValue + "_test"
    }
    "can unset property"{
        proxy.set(sysKey, sysValue + "_test")
        var proxy_path = proxy.get(sysKey)
        proxy_path shouldBe sysValue + "_test"
        proxy.unset(sysKey)
        proxy_path = proxy.get(sysKey)
        proxy_path shouldBe sysValue
    }
    "can get null"{
        proxy.get(nonExistedKey) shouldBe null
    }
    "can get default"{
        proxy.getOrDefault(nonExistedKey, defaultValue) shouldBe defaultValue
    }
    "can set new value"{
        proxy.set(nonExistedKey, defaultValue)
        proxy.get(nonExistedKey) shouldBe defaultValue
    }
    "can get typed"{
        proxy.set(nonExistedKey, true)
        proxy.get<Boolean>(nonExistedKey) shouldBe true
    }
    "can get typed with conversion"{
        proxy.set(nonExistedKey, "123")
        proxy.get<Int>(nonExistedKey) shouldBe 123
    }
    "annotated with existed env"{
        val value = proxy.getAnnotated(sysKey)
        value.requestedName shouldBe sysKey
        value.internalName shouldBe sysKey
        value.value shouldBe sysValue
        value.evidence shouldBe "env"
        value.defined shouldBe true
        value.overriden shouldBe false
        value.sourceInstance shouldBe proxy
    }
    "annotated with non existed env"{
        val value = proxy.getAnnotated(nonExistedKey)
        value.value shouldBe null
        value.evidence shouldBe "env"
        value.defined shouldBe false
        value.overriden shouldBe false
        value.sourceInstance shouldBe proxy
    }
    "annotated with override"{
        proxy.set(nonExistedKey, defaultValue)
        val value = proxy.getAnnotated(nonExistedKey)
        value.value shouldBe defaultValue
        value.evidence shouldBe "env"
        value.defined shouldBe true
        value.overriden shouldBe true
        value.sourceInstance shouldBe proxy
    }
    "annotated with override null"{
        proxy.set(sysKey, null)
        val value = proxy.getAnnotated(sysKey)
        value.value shouldBe null
        value.evidence shouldBe "env"
        value.defined shouldBe true
        value.overriden shouldBe true
    }
    "has singleton instance"{
        EnvProxy.Instance shouldBeSameInstanceAs EnvProxy.Instance
        EnvProxy.Instance::class.java shouldBe EnvProxy::class.java
    }
    "typed get"{
        proxy.set(nonExistedKey, "true")
        proxy.get(nonExistedKey, Boolean::class.java) shouldBe true
    }
    "ensure throws if not exists"{
        shouldThrow<NullPointerException> {
            proxy.ensure(nonExistedKey)
        }
    }
    "ensure works as get if not exists"{
        proxy.ensure(sysKey) shouldBe sysValue
    }
    "typed ensure both direct and reified)"{
        proxy.set(nonExistedKey, "true")
        proxy.ensure(nonExistedKey, Boolean::class.java) shouldBe true
        proxy.ensure<Boolean>(nonExistedKey) shouldBe true

    }
}) {
    override fun beforeTest(testCase: TestCase) {
        proxy.clear()
    }

    companion object {
        val exitedProperty = System.getenv().entries.first { it.value != null && it.value.isNotBlank() }
        val sysKey = exitedProperty.key
        val sysValue = exitedProperty.value
        val nonExistedKey = sysKey + "NOT_EXISTED"
        val defaultValue = "DEFAULT VALUE"
        val proxy = EnvProxy()
    }
}
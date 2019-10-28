package codes.spectrum.sources

import codes.spectrum.api.exceptions.FieldNotFoundInOptionsException
import codes.spectrum.api.exceptions.FieldWithTypeNotFoundInOptionsException
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import java.lang.NullPointerException


class SourceContextTest: StringSpec ({

    "проверяем метод getFromOptions, вовращает поле" {
        val context = SourceContext(SourceQuery(""), SourceResult(data = ""))
        context.options["str"] = "StringField"

        val strResult = context.getFromOptions<String>("str")
        strResult shouldBe  "StringField"
    }

    "проверяем метод getFromOptions, вовращает NullPointerException" {
        val context = SourceContext(SourceQuery(""), SourceResult(data = ""))


        val exception = shouldThrow<NullPointerException> {
            context.getFromOptions<String>("str", true)
        }

        exception.javaClass.simpleName shouldBe "NullPointerException"
        exception.message shouldBe "isReturnNull flag on"
    }

    "проверяем метод getFromOptions, вовращает FieldWithTypeNotFoundInOptionsException" {
        val context = SourceContext(SourceQuery(""), SourceResult(data = ""))
        context.options["str"] = "StringField"

        val exception = shouldThrow<FieldWithTypeNotFoundInOptionsException> {
            context.getFromOptions<Int>("str")
        }

        exception.javaClass.simpleName shouldBe "FieldWithTypeNotFoundInOptionsException"
        exception.message shouldBe "Field with name \"str\" and type \"java.lang.Integer\" in options doesn't exist!"
    }

    "проверяем метод getFromOptions, вовращает FieldNotFoundInOptionsException" {
        val context = SourceContext(SourceQuery(""), SourceResult(data = ""))

        val exception = shouldThrow<FieldNotFoundInOptionsException> {
            context.getFromOptions<Int>("str")
        }

        exception.javaClass.simpleName shouldBe "FieldNotFoundInOptionsException"
        exception.message shouldBe "Field with name \"str\" in options doesn't exist!"
    }

    "проверяем метод getFromOptionsOrNull, вовращает результат" {
        val context = SourceContext(SourceQuery(""), SourceResult(data = ""))
        context.options["str"] = "StringField"

        val strResult = context.getFromOptionsOrNull<String>("str")
        strResult shouldBe  "StringField"
    }

    "проверяем метод getFromOptionsOrNull, вовращает null" {
        val context = SourceContext(SourceQuery(""), SourceResult(data = ""))

        val strResult = context.getFromOptionsOrNull<String>("str")
        strResult shouldBe  null
    }
})
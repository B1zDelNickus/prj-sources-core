package codes.spectrum.sources.json

import io.kotlintest.matchers.maps.shouldContainKey
import io.kotlintest.matchers.maps.shouldNotContainKey
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

class JsetExtensionTest : StringSpec({
    "can set and get single name"{
        val jhash = mutableMapOf<String, Any?>()
        jhash.jset("x", 1)
        jhash["x"] shouldBe 1
        jhash.jget("x") shouldBe 1
    }
    "can set and get by path"{
        val jhash = mutableMapOf<String, Any?>()
        jhash.jset("x.y", 1)
        (jhash["x"] as Map<String, Any?>)["y"] shouldBe 1
        jhash.jget("x.y") shouldBe 1
    }

    "can merge"{
        val target = object {
            val x = 1;
            val y = object {
                val a = 2
            };
            val u = object {
                val a = 2;
                val b = 3
            }
        }.jsonifyObject()
        val source = object {
            val z = 1;
            val y = 2;
            val u = object {
                val a = 4;
                val c = 5
            }
        }.jsonifyObject()
        target.jmerge(source)
        target.jget("x") shouldBe 1
        target.jget("z") shouldBe 1
        target.jget("y") shouldBe 2
        target.jget("u.a") shouldBe 4
        target.jget("u.b") shouldBe 3
        target.jget("u.c") shouldBe 5
    }

    "can jsonify complex objects"{
        val jhash = JsonHash()
        jhash.jset("x.y", object {
            val a = 1;
            val b = object {
                val c = 3
            }
        })
        jhash.jget("x.y.a") shouldBe 1
        jhash.jget("x.y.b.c") shouldBe 3
    }
    "jexists works"{
        val jhash = JsonHash()
        jhash.jset("x.y", object {
            val a = 1;
            val b = object {
                val c = 3
            }
        })
        jhash.jexists("x.y.b.c") shouldBe true
        jhash.jexists("x.y.b.d") shouldBe false
    }

    "jsetDefault and jset"{
        val jhash = JsonHash()
        jhash.jset("x.y", 1)
        jhash.jset("x.y", 2)
        jhash.jsetDefault("x.y", 3)
        jhash.jsetDefault("x.z", 3)
        jhash.jget("x.y") shouldBe 2
        jhash.jget("x.z") shouldBe 3
    }

    "can avoid recursion"{
        class A(val x: String, var nest: A? = null)

        val a = A("a")
        val b = A("b")
        a.nest = b
        b.nest = b // recursion
        val json = a.jsonify() as JsonHash
        json.jget("x") shouldBe "a"
        json.jget("nest.x") shouldBe "b"
        json.jget("nest.nest.__recursiveStack") shouldBe true
    }
    "can write and get arrays"{
        val json = object {
            val a = arrayOf(1, 2, 3)
            val x = object {
                val y = arrayOf(1, "x", object {
                    val z = 3;
                    val u = mapOf("n" to "m")
                })
            }
        }.jsonify() as JsonHash
        json.jget("a[2]") shouldBe 3
        json.jget("x.y[0]") shouldBe 1
        json.jget("x.y[1]") shouldBe "x"
        json.jget("x.y[2].z") shouldBe 3
        json.jget("x.y[2].u.n") shouldBe "m"
    }

    "null on null with subpath"{
        val json = (object {}).jsonifyObject()
        json.jget("a[2]") shouldBe null
        json.jget("a.z") shouldBe null
    }

    "option nullEmptyLists true"{
        val json = object {
            val x = arrayOf<Any>()
        }.jsonifyObject(JsonifyOptions.Instance.copy(nullEmptyLists = true))
        json shouldNotContainKey "x"
    }

    "option nullEmptyLists false"{
        val json = object {
            val x = arrayOf<Any>()
        }.jsonifyObject(JsonifyOptions.Instance.copy(nullEmptyLists = false))
        json shouldContainKey "x"
        json.jget("x")!!.javaClass shouldBe mutableListOf<Any>().javaClass
    }

    "option singleListToValue true"{
        val json = object {
            val x = arrayOf(1)
        }.jsonifyObject(JsonifyOptions.Instance.copy(singleListToValue = true))
        json.jget("x") shouldBe 1
    }

    "option singleListToValue false"{
        val json = object {
            val x = arrayOf(1)
        }.jsonifyObject(JsonifyOptions.Instance.copy(singleListToValue = false))
        json.jget("x[0]") shouldBe 1
    }

    class SomeObj(val x: Int = 1) {
        override fun hashCode(): Int {
            return x
        }
    }
    "option writeObjMarkers true"{
        val json = SomeObj(2).jsonifyObject(JsonifyOptions.Instance.copy(writeObjMarkers = true))
        json.jget("x") shouldBe 2
        json.jget(JsonifyOptions.Instance.objMarker) shouldBe "${SomeObj::class.java.name}@2"
    }

    "option writeObjMarkers true and custom marker"{
        val json = SomeObj(2).jsonifyObject(JsonifyOptions.Instance.copy(writeObjMarkers = true, objMarker = "clz"))
        json.jget("x") shouldBe 2
        json.jget("clz") shouldBe "${SomeObj::class.java.name}@2"
    }


    "option writeObjMarkers false"{
        val json = object {
            val x = arrayOf(1)
        }.jsonifyObject(JsonifyOptions.Instance.copy(writeObjMarkers = false))
        json shouldNotContainKey (JsonifyOptions.Instance.objMarker)
    }

    "option stringToList true"{
        val json = object {
            val x = "[1,2]"
        }.jsonifyObject(JsonifyOptions.Instance.copy(stringToList = true))
        json.jget("x[0]") shouldBe "1"
        json.jget("x[1]") shouldBe "2"
    }

    "option stringToList true stringToListRequireBraces true"{
        val json = object {
            val x = "1,2"
        }.jsonifyObject(JsonifyOptions.Instance.copy(stringToList = true, stringToListRequireBraces = true))
        json.jget("x") shouldBe "1,2"
    }
    "option stringToList true stringToListRequireBraces false"{
        val json = object {
            val x = "1,2"
        }.jsonifyObject(JsonifyOptions.Instance.copy(stringToList = true, stringToListRequireBraces = false))
        json.jget("x[0]") shouldBe "1"
        json.jget("x[1]") shouldBe "2"
    }


    "option stringToList true and custom delimiter"{
        val json = object {
            val x = "[1,2;3]"
        }.jsonifyObject(JsonifyOptions.Instance.copy(stringToList = true, stringToListDelimiter = ";"))
        json.jget("x[0]") shouldBe "1,2"
        json.jget("x[1]") shouldBe "3"
    }

    "option stringToList false"{
        val json = object {
            val x = "1,2"
        }.jsonifyObject(JsonifyOptions.Instance.copy(stringToList = false))
        json.jget("x") shouldBe "1,2"
    }

    "option skipNulls true"{
        val json = object {
            val x = null
        }.jsonifyObject(JsonifyOptions.Instance.copy(skipNulls = true))
        json shouldNotContainKey "x"
    }

    "option skipNulls false"{
        val json = object {
            val x = null
        }.jsonifyObject(JsonifyOptions.Instance.copy(skipNulls = false))
        json shouldContainKey "x"
    }

    "option stringTrim true"{
        val json = object {
            val x = " x "
        }.jsonifyObject(JsonifyOptions.Instance.copy(stringTrim = true))
        json.jget("x") shouldBe "x"
    }

    "option stringTrim false"{
        val json = object {
            val x = " x "
        }.jsonifyObject(JsonifyOptions.Instance.copy(stringTrim = false))
        json.jget("x") shouldBe " x "
    }


    "option emptyStringIsNull BlankToNull and skipNull true"{
        val json = object {
            val x = "   "
        }.jsonifyObject(JsonifyOptions.Instance.copy(
            nullStringLevel = NullStringLevel.BlankToNull,
            skipNulls = true
        ))
        json shouldNotContainKey "x"
    }
    "option emptyStringIsNull EmptyToNull and skipNull true and stringTrim false"{
        val json = object {
            val x = "   ";
            val y = ""
        }.jsonifyObject(JsonifyOptions.Instance.copy(
            nullStringLevel = NullStringLevel.EmptyToNull,
            skipNulls = true,
            stringTrim = false
        ))
        json shouldNotContainKey "y"
        json shouldContainKey "x"
        json.jget("x") shouldBe "   "
    }

    "option emptyStringIsNull EmptyToNull and skipNull true and stringTrim true"{
        val json = object {
            val x = "   ";
            val y = ""
        }.jsonifyObject(JsonifyOptions.Instance.copy(
            nullStringLevel = NullStringLevel.EmptyToNull,
            skipNulls = true,
            stringTrim = true
        ))
        json shouldNotContainKey "y"
        json shouldNotContainKey "x"
    }

    "option emptyStringIsNull BlankToEmpty"{
        val json = object {
            val x = "   "
        }.jsonifyObject(JsonifyOptions.Instance.copy(
            nullStringLevel = NullStringLevel.BlankToEmpty
        ))
        json shouldContainKey "x"
        json.jget("x") shouldBe ""
    }



    "error jget - not indexed value"{
        shouldThrow<Exception> {
            val json = (object {
                val x = 1
            }).jsonifyObject()
            json.jget("x[1]")
            Unit
        }
    }

    "error jget - not object value on path"{
        shouldThrow<Exception> {
            val json = (object {
                val x = 1
            }).jsonifyObject()
            json.jget("x.z")
            Unit
        }
    }
})
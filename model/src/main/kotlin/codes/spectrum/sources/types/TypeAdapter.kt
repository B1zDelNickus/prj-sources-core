package codes.spectrum.sources.types

import codes.spectrum.serialization.json.Json
import java.math.BigDecimal
import java.time.LocalDate

object TypeAdapter {
    fun <T> convertTo(src:Any, target:Class<T>):T{
        val clz = src.javaClass
        var result  = when{
            clz == target -> src
            target == String::class.java -> src.toString()
            target == Boolean::class.java -> src.toString().toBoolean()
            target == Int::class.java -> src.toString().toInt()
            target == Long::class.java -> src.toString().toLong()
            target == BigDecimal::class.java -> src.toString().toBigDecimal()
            target == LocalDate::class.java ->  LocalDate.parse(src.toString())
            !target.isPrimitive && !target.isInterface -> Json.read(src.toString(), target)
            else -> throw Exception("Cannot convert source type ${clz.name} to target class ${target.name} with value ${src}")
        }as T
        return result
    }
}
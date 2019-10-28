package codes.spectrum.sources.core.source

import codes.spectrum.serialization.jsonschema.JsonSchemaIgnore
import codes.spectrum.serialization.jsonschema.JsonSchemaIndex
import codes.spectrum.serialization.jsonschema.JsonSchemaTitle
import com.google.gson.annotations.SerializedName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

/**
 * Фабрика параметров
 */
object ParameterFactory {
    /**
     * Методы получения списка параметров (как из объекта класса, так и из класса)
     */
    fun getParameters(clazz: Class<*>, names: List<String> = emptyList()): List<Parameter> =
        getParameters(clazz.kotlin, names)

    fun getParameters(clazz: KClass<*>, names: List<String> = emptyList()): List<Parameter> =
        clazz.memberProperties
            .filter { (names.isEmpty() || names.contains(it.name)) && it.findAnnotation<JsonSchemaIgnore>() == null }
            .map { property ->
                val name = property.javaField?.getAnnotation(SerializedName::class.java)?.value ?: property.name
                Parameter(
                    name,
                    property.findAnnotation<JsonSchemaTitle>()?.title ?: name,
                    property.findAnnotation<JsonSchemaIndex>()?.index ?: if(names.isNotEmpty()) names.indexOf(name) else 0,
                    property.javaField?.type
                )
            }.sortedBy { it.index }

    fun getParameters(obj: Any?, names: List<String> = emptyList()): List<Parameter> =
        if(obj != null)
            obj::class.memberProperties
                .filter { (names.isEmpty() || names.contains(it.name)) && it.findAnnotation<JsonSchemaIgnore>() == null }
                .map { property ->
                    val name = property.javaField?.getAnnotation(SerializedName::class.java)?.value ?: property.name
                    val value = (property as KProperty1<Any, *>).get(obj)?.let {
                        if(it::class.java.isEnum) {
                            it::class.java.fields.firstOrNull { field -> field.name == it.toString() }
                                ?.let { it.getAnnotation(SerializedName::class.java)?.value ?: it }
                        }
                        else
                            it
                    }

                    Parameter(
                        name,
                        property.findAnnotation<JsonSchemaTitle>()?.title ?: name,
                        property.findAnnotation<JsonSchemaIndex>()?.index ?: if(names.isNotEmpty()) names.indexOf(name) else 0,
                        property.javaField?.type,
                        value
                    )
                }.sortedBy { it.index }
        else
            emptyList()
}
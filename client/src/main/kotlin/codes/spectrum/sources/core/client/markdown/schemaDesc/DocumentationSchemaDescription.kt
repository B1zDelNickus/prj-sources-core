package codes.spectrum.sources.core.client.markdown.schemaDesc

import codes.spectrum.schemaDesc.DocFormat
import codes.spectrum.schemaDesc.IDocTitle
import codes.spectrum.serialization.json.Json
import codes.spectrum.serialization.jsonschema.JsonProperty
import codes.spectrum.serialization.jsonschema.JsonSchema
import codes.spectrum.serialization.jsonschema.JsonSchemaGenerator
import org.slf4j.LoggerFactory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.TypeVariable
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaType

class DocumentationSchemaDescription<T : Any>(
        val obj: T,
        val schemas: Map<Class<*>, JsonSchema> = mapOf(),
        val classDesc: List<ClassDescription> = listOf(),
        val fieldDesc: List<FieldDescription> = listOf(),
        val enumDesc: List<EnumDescription> = listOf()
) {
    override fun toString() = Json.stringify(this)

    data class Builder<T : Any>(
            var obj: T,
            var root: String = "",
            var exclude: MutableMap<Class<*>, MutableSet<String>> = mutableMapOf(),
            var enumTitled: MutableMap<Enum<*>, String> = mutableMapOf(),
            var generator: JsonSchemaGenerator = JsonSchemaGenerator(),
            val schemas: MutableMap<Class<*>, JsonSchema> = mutableMapOf(),
            var bannedPaths: MutableList<String> = mutableListOf(),
            var classDesc: MutableList<ClassDescription> = mutableListOf(),
            var fieldDesc: MutableList<FieldDescription> = mutableListOf(),
            var enumDesc: MutableList<EnumDescription> = mutableListOf()
    ) {
        fun build(): DocumentationSchemaDescription<T> {
            buildDescription()

            return DocumentationSchemaDescription(
                    obj = obj,
                    schemas = schemas,
                    classDesc = classDesc,
                    fieldDesc = fieldDesc,
                    enumDesc = enumDesc
            )
        }

        private fun buildClassName(clazz: Class<*>?, propertyName: String? = null, property: JsonProperty? = null): String {
            return clazz?.simpleName
                    ?: property?.description
                    ?: property?.title
                    ?: error("unkwown objectClass $clazz; $propertyName; $property")
        }

        private fun buildDescription() {
            val resultFields = mutableMapOf<String, FieldDescription>()
            val resultEnums = mutableMapOf<String, EnumDescription>()
            val resultClasses = mutableMapOf<String, ClassDescription>()
            val resultClassProperties = mutableMapOf<String, MutableMap<String, FieldDescription>>()

            fun build(clazz: Class<*>, examples: List<Any>, banned: List<String>, path: String = "", title: String = "") {
                logger.info("start building for $clazz, path - $path")
                val schema = schemas.computeIfAbsent(clazz) { generator.build(it) }

//                val classFields = mutableListOf<FieldDescription>()
                val className = buildClassName(clazz)
                val classDescription = resultClasses.computeIfAbsent(className) {
                    resultClassProperties[className] = mutableMapOf()
                    ClassDescription(clazz = className, description = schemas[clazz]?.description, fields = mutableListOf())
                }
                val classFieldDescription = resultClassProperties[className]!!
                val bannedList = banned.map { it.split(".") }

                // Рекурсивно обходим поля объекта (классов)
                schema.properties.filter { it.key !in banned }.forEach { (name, property) ->
                    val newBanned = bannedList.filter { it.first() == name }.map { it.drop(1).joinToString(".") }
                    val newPath = listOf(path, name)
                            .filter { it.isNotBlank() }
                            .joinToString(".")
                    val propertyTitle = property.description ?: property.title ?: "$name"
                    val newTitle = listOf(title, propertyTitle)
                            .filter { it.isNotBlank() }
                            .joinToString(". ")

                    val memberProperty = clazz.kotlin.memberProperties.single { it.name == name }
                    val values = examples.mapNotNull { memberProperty.getter.call(it) }
                    val value = values.firstOrNull()
                    val propType = value?.javaClass ?: memberProperty.returnType.clazz()
                    val example = value?.toString() ?: ""

                    val (ref, retExample) = when {
                        property.ref != null -> {
                            build(clazz = propType, examples = values, banned = newBanned, path = newPath, title = newTitle)

                            Pair(buildClassName(propType, name, property), null)
                        }
                        property.items?.ref != null -> {
                            val javaType = memberProperty.returnType.javaType as ParameterizedType
                            val collectionClass = javaType.rawType as Class<*>
                            val argument = javaType.actualTypeArguments.single() as Class<*>

                            val newExamples = when {
                                Collection::class.java.isAssignableFrom(collectionClass) -> values.mapNotNull { it as? Collection<Any> }.flatten()
                                java.util.Collection::class.java.isAssignableFrom(collectionClass) -> values.mapNotNull { it as? java.util.Collection<Any> }.flatten()
                                else -> error("for ${clazz.simpleName} property \"${name}\" not supported collection class ${property.items?.ref} - $propType")
                            }

                            build(clazz = argument, examples = newExamples, banned = newBanned, path = "$newPath[]", title = newTitle)

                            when {
                                collectionClass?.isArray == true -> Pair(buildClassName(collectionClass.componentType, name, property), null)
                                collectionClass != null && (Collection::class.java.isAssignableFrom(collectionClass) || java.util.Collection::class.java.isAssignableFrom(collectionClass)) -> {
                                    val javaType = memberProperty.returnType.javaType as ParameterizedType
                                    val argument = javaType.actualTypeArguments.single() as Class<*>
                                    Pair(buildClassName(argument, name, property), null)
                                }
                                else -> error("unknown array type $clazz; $name; $propType; $value")
                            }
                        }
                        else -> {
                            if (property.enum?.isNotEmpty() == true) {
                                val titled = if (propType != null && propType.isEnum) {
                                    propType.enumConstants
                                            .map {
                                                val titled = if (it is IDocTitle) it.docTitle else null
                                                "$it" to (enumTitled[it] ?: titled ?: "$it")
                                            }
                                            .toMap()
                                } else {
                                    property.enum!!
                                            .map { "$it" to (enumTitled[it] ?: "$it") }
                                            .toMap()
                                }

                                // Добавляем enum если его не было
                                val description = EnumDescription(
                                        className = buildClassName(propType, name, property),
                                        values = titled,
                                        example = example
                                )

                                if (description.className !in resultEnums)
                                    resultEnums[description.className] = description
                            }

                            val ref = if (property.enum?.isNotEmpty() == true) buildClassName(propType, name, property) else null

                            Pair(ref, example)
                        }
                    }
                    val format = memberProperty.findAnnotation<DocFormat>()?.format

                    if (property.ref == null && property.items?.ref == null) {
                        resultFields[newPath] = FieldDescription(
                                field = "$root.$newPath",
                                type = property.type ?: "",
                                description = newTitle,
                                example = retExample,
                                ref = ref,
                                format = format
                        )
                    }

                    if ((classFieldDescription[name]?.example ?: "").isEmpty()) {
                        classFieldDescription[name] = FieldDescription(
                                field = name,
                                type = property.type ?: "",
                                description = propertyTitle,
                                example = retExample,
                                ref = ref,
                                format = format
                        )
                    }

                }
            }

            build(clazz = obj.javaClass, examples = listOf(obj), banned = bannedPaths)

            resultClasses.keys.forEach { clz ->
                val clazz = resultClasses[clz]!!
                val props = resultClassProperties[clz]!!
                resultClasses[clz] = ClassDescription(clazz.clazz, clazz.description, props.values.toList())
            }

            fieldDesc = resultFields.values.toMutableList()
            classDesc = resultClasses.values.toMutableList()
            enumDesc = resultEnums.values.toMutableList()
        }

        fun KType.clazz(): Class<*> {
            return when (javaType) {
                is ParameterizedType -> (javaType as ParameterizedType).rawType as Class<*>
                is TypeVariable<*> -> (javaType as TypeVariable<*>).genericDeclaration as Class<*>
                is Class<*> -> javaType as Class<*>
                else -> javaClass
            }
        }

        infix fun Enum<*>.to(title: String) {
            enumTitled[this] = title
        }

        fun exclude(clazz: Class<*>, field: String) {
            exclude.computeIfAbsent(clazz) { mutableSetOf() }.add(field)
        }

        inline fun <reified T> exclude(field: String) = exclude(T::class.java, field)

        operator fun set(enums: Enum<*>, title: String) {
            enumTitled[enums] = title
        }
    }

    companion object {
        operator fun <T : Any> invoke(obj: T, populate: Builder<T>.() -> Unit): DocumentationSchemaDescription<T> {
            val builder = Builder(obj)

            builder.obj = obj

            builder.populate()
            return builder.build()
        }

        private val logger = LoggerFactory.getLogger("json.schema.description")
    }
}
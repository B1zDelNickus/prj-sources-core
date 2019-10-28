package codes.spectrum.sources.json

import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import java.util.stream.Stream
import kotlin.reflect.full.memberProperties
import kotlin.streams.toList

typealias JsonHash = HashMap<String, Any?>

enum class NullStringLevel {
    BlankToNull,
    BlankToEmpty,
    EmptyToNull,
    Empty;

    fun convert(value: String?): String? {
        if (null == value) return null
        if (value.isBlank()) {
            return when (this) {
                BlankToNull -> null
                BlankToEmpty -> ""
                EmptyToNull -> if (value.isEmpty()) null else value
                Empty -> value
            }
        }
        return value
    }
}
data class JsonifyOptions(
    // рассматривать пустые коллекции как null
    val nullEmptyLists: Boolean = false,
    // рассматривать коллекции из одного элемента как единичные значения
    val singleListToValue: Boolean = false,
    // рассматривать запятые в строках как признак для разделения на подэлементы в список
    val stringToList: Boolean = false,
    val stringToListDelimiter: String = ",",
    val stringToListRequireBraces: Boolean = true,
    val stringDetectJson: Boolean = false,
    // пропуск NULL
    val skipNulls: Boolean = true,
    // триминг строк
    val stringTrim: Boolean = true,
    val nullStringLevel: NullStringLevel = NullStringLevel.BlankToNull,
    val writeObjMarkers: Boolean = true,
    val objMarker: String = "__obj"
) {

    companion object {
        val Instance = JsonifyOptions()
        val HttpQuery = JsonifyOptions(
            nullEmptyLists = true,
            singleListToValue = true,
            stringToList = true,
            writeObjMarkers = false,
            stringDetectJson = true
        )
    }
}

class Jsonify(val defaultOptions: JsonifyOptions = JsonifyOptions.Instance) {
    fun jsonify(value: Any?, options: JsonifyOptions = JsonifyOptions.Instance, visitStack: Stack<Any?> = Stack()): Any? {
        return when {
            value == null -> null
            value is String -> jsonifyString(value, options)
            value is Boolean -> value
            value.javaClass.isPrimitive -> value
            value is Number -> value
            value is LocalDate -> value
            value is Date -> value
            value is BigDecimal -> value
            value is Map<*, *> -> jsonifyMap(value, options, visitStack)
            value is Iterable<*> -> jsonifyList(value, options, visitStack)
            value is Array<*> -> jsonifyList(value.asIterable(), options, visitStack)
            value is Stream<*> -> jsonifyList(value.toList(), options, visitStack)
            value is Sequence<*> -> jsonifyList(value.toList(), options, visitStack)
            value is Iterator<*> -> jsonifyList(value.asSequence().asIterable(), options, visitStack)
            else -> jsonifyObject(value, options, visitStack)
        }
    }

    private fun objMarker(value: Any, options: JsonifyOptions): String {
        return "${value.javaClass.name}@${Integer.toHexString(value.hashCode())}"
    }

    private fun recursionMarker(value: Any, options: JsonifyOptions): JsonHash {
        val result = JsonHash()
        result["__recursiveStack"] = true
        applyObjMarker(result, value, options)
        return result
    }

    private fun applyObjMarker(target: MutableMap<String, Any?>, value: Any, options: JsonifyOptions) {
        if (options.writeObjMarkers) {
            target[options.objMarker] = objMarker(value, options)
        }
    }

    private fun jsonifyObject(value: Any, options: JsonifyOptions, visitStack: Stack<Any?> = Stack()): Any? {
        var result = JsonHash()
        applyObjMarker(result, value, options)
        visitStack.push(value)
        for (p in value.javaClass.kotlin.memberProperties) {
            val getter = p.getter
            val value = p.getter.call(value)
            if (null == value) {
                if (options.skipNulls) continue
                else result[p.name] = null
            } else if (visitStack.contains(value)) {
                result[p.name] = recursionMarker(value, options)
            } else {
                val realvalue = value.jsonify(options, visitStack)

                if (options.skipNulls && realvalue == null) {
                    continue
                }
                result[p.name] = realvalue
            }
        }
        visitStack.pop()
        return result
    }

    private fun jsonifyList(value: Iterable<*>, options: JsonifyOptions, visitStack: Stack<Any?> = Stack()): Any? {
        val list = value.toList()
        if (list.isEmpty()) {
            if (options.nullEmptyLists) return null
        }
        if (list.size == 1 && options.singleListToValue) {
            return jsonify(list.first(), options)
        }
        return list.map { jsonify(it, options) }.toMutableList()
    }

    private fun jsonifyMap(value: Map<*, *>, options: JsonifyOptions, visitStack: Stack<Any?> = Stack()): Any? {
        return value.entries.map { it.key.toString() to it.value.jsonify(options, visitStack) }.toMap()
    }

    private fun jsonifyString(value: String, options: JsonifyOptions): Any? {
        var realValue: String? = value
        if (options.stringTrim) {
            realValue = realValue!!.trim()
        }
        realValue = options.nullStringLevel.convert(realValue)
        realValue?.let {
            if (options.stringToList && it.contains(options.stringToListDelimiter)) {
                val hasbraces = it.startsWith("[") && it.endsWith("]")
                if (!options.stringToListRequireBraces || hasbraces) {
                    var prelist = it
                    if (hasbraces) {
                        prelist = prelist.substring(1, it.length - 1)
                    }
                    return prelist.split(options.stringToListDelimiter).jsonify(options)
                }
            }
        }
        return realValue
    }

    companion object {
        val Instance = Jsonify()
    }
}

fun Any?.jsonify(options: JsonifyOptions = JsonifyOptions.Instance, visitStack: Stack<Any?> = Stack()) = Jsonify.Instance.jsonify(this, options, visitStack)
fun Any?.jsonifyObject(options: JsonifyOptions = JsonifyOptions.Instance, visitStack: Stack<Any?> = Stack()) = Jsonify.Instance.jsonify(this, options, visitStack) as JsonHash

fun MutableMap<*, *>.jexists(path: String): Boolean {
    return jget(path) != null
}

fun MutableMap<*, *>.jsetDefault(path: String, value: Any?, options: JsonifyOptions = JsonifyOptions.Instance) {
    if (!jexists(path)) {
        jset(path, value, options)
    }
}

fun MutableMap<*, *>.jset(path: String, value: Any?, options: JsonifyOptions = JsonifyOptions.Instance) {
    synchronized(this) {
        val realValue = value.jsonify(options)
        if (options.skipNulls && null == realValue) return
        val pathParts = JPathPart.parsePath(path)
        var current = this as MutableMap<String, Any?>
        for (p in pathParts.dropLast(1)) {
            if (!current.containsKey(p.name) || current[p.name] == null) {
                current[p.name] = if (p.isIndexed) mutableListOf<Any?>() else JsonHash()
            }
            if (p.isIndexed) {
                if (!(current[p.name] is MutableList<*>)) {
                    throw Exception("Invalid target type ${current[p.name]!!.javaClass.name} for jpath ${path}")
                }
                val list = current[p.name] as MutableList<Any?>
                if (list.size > p.index) {
                    if (!(list[p.index] is MutableMap<*, *>)) {
                        throw Exception("Invalid target type ${current[p.name]!!.javaClass.name} for jpath ${path}")
                    }
                    current = list[p.index] as MutableMap<String, Any?>
                } else {
                    if (list.size == p.index) {
                        current = JsonHash()
                        list.add(current)
                    }
                }
            } else {
                if (!(current[p.name] is MutableMap<*, *>)) {
                    throw Exception("Invalid target type ${current[p.name]!!.javaClass.name} for jpath ${path}")
                }
                current = current[p.name] as MutableMap<String, Any?>
            }
        }
        current[pathParts.last().name] = realValue
    }
}


class JPathPart(val name: String, val index: Int = -1) {
    val isIndexed = index >= 0
    fun resove(map: Map<String, Any?>): Any? {
        if (!(map.containsKey(name)) || map[name] == null) return null
        var result = map[name]
        if (isIndexed) {
            if (result is List<*>) {
                result = result[index]
            } else throw Exception("Item is not indexed for ${name}[$index]")
        }
        return result
    }

    override fun toString(): String {
        return if (isIndexed) "${name}[${index}]" else name
    }

    companion object {
        fun create(p: String): JPathPart {
            if (p.contains("[")) {
                val split = p.dropLast(1).split("[")
                return JPathPart(split[0], split[1].toInt())
            } else {
                return JPathPart(p, -1)
            }
        }

        fun parsePath(path: String): List<JPathPart> {
            return path.split(".").map { create(it) }
        }
    }
}

fun MutableMap<*, *>.jget(path: String): Any? {
    val pathParts = JPathPart.parsePath(path)
    var current = this as Map<String, Any?>
    for (p in pathParts.dropLast(1)) {
        val next = p.resove(current)
        if (null == next) return null
        if (next is Map<*, *>) {
            current = next as Map<String, Any?>
        } else {
            throw Exception("Not object on path ${p}")
        }
    }
    return pathParts.last().resove(current)
}

fun MutableMap<*, *>.jmerge(antother: Map<*, *>, override: Boolean = true) {
    val target = this as MutableMap<String, Any?>
    val source = antother as Map<String, Any?>
    for (e in source.entries) {
        if (!this.containsKey(e.key)) {
            this[e.key] = e.value.jsonify()
        } else {
            if (this[e.key] is Map<*, *> && e.value is Map<*, *>) {
                (this[e.key] as MutableMap<*, *>).jmerge(e.value as Map<*, *>, override)
            } else if (override) {
                this[e.key] = e.value.jsonify()
            }
        }
    }
}
package codes.spectrum.sources.config

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.exceptions.NotFoundEnvElementException
import codes.spectrum.sources.types.TypeAdapter
import kotlin.reflect.KClass

inline fun <reified T> IConfig.get(name:String) = get(name, T::class.java)
inline fun <reified T> IConfig.ensure(name:String) = ensure(name, T::class.java)
inline fun <reified T> IConfig.getOrDefault(name:String, default:T) = getOrDefault(name, T::class.java, default)

interface IConfig : IKonveyorEnvironment {

    fun getAnnotated(name:String): AnnotatedProperty

    fun get(name:String): Any? = getAnnotated(name).value

    fun ensure(name:String): Any = get(name) ?: throw NullPointerException("Property $name not found")

    fun <T> get(name:String, clz:Class<T>): T? {
        val result = get(name) ?: return null
        return TypeAdapter.convertTo(result, clz)
    }

    fun <T> getOrDefault(name:String, clz:Class<T>, default:T) = get(name,clz) ?: default

    fun <T> ensure(name:String, clz:Class<T>) = TypeAdapter.convertTo(ensure(name), clz)


    override fun has(name:String): Boolean =
        try{
            ensure(name)
            true
        }
        catch (e: Throwable) {
            false
        }

    override fun has(name:String, klazz: KClass<*>): Boolean =
        try{
            ensure(name, klazz.java)
            true
        }
        catch (e: Throwable) {
            false
        }

    override fun <T> get(name:String, klazz: KClass<*>): T = get(name) as? T ?: throw NotFoundEnvElementException(name)
}

val GlobalConfig: IConfig by lazy {EnvProxy.Instance}
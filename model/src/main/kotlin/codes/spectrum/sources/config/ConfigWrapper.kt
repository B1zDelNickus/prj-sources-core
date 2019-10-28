package codes.spectrum.sources.config

/**
 * Обёртка для IConfig, позволяющая дополнить пользовательским словарём свойств свойства из IConfig.
 * Если свойства имеют одинаковое имя, свойство из обёртки перекрывает свойство из конфига.
 * Если свойства нет в обёртке, оно ищется в конфиге.
 * Можно также передать произвольное evidence, чтобы отличать один конфиг от другого.
 */
class ConfigWrapper(private val parent: IConfig, val evidence: String = "wrapper", setup: MutableMap<String, Any?>.()->Unit = {}): IConfig {
    private val extraProperties = mutableMapOf<String, Any?>().apply { setup() }
    
    override fun getAnnotated(name: String): AnnotatedProperty {
        if (extraProperties.containsKey(name))
            return AnnotatedProperty(
                value = extraProperties[name],
                requestedName = name,
                internalName = name,
                overriden = true,
                defined = true,
                evidence = evidence
            ).apply { sourceInstance = this@ConfigWrapper }
        return parent.getAnnotated(name)
    }
}
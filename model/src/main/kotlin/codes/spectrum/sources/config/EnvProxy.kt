package codes.spectrum.sources.config


class EnvProxy : IConfig {

    private val overrider = mutableMapOf<String, Any?>()

    override fun getAnnotated(name: String): AnnotatedProperty {
        var value: Any? = null
        var overriden = false
        var defined = false
        if (isOverriden(name)) {
            synchronized(overrider) {
                value = overrider[name]
                overriden = true
                defined = true
            }
        } else if (System.getenv().containsKey(name)) {
            value = System.getenv().get(name)
            defined = true
        }
        return AnnotatedProperty(value, name, name, "env", defined=defined, overriden = overriden).apply {
            sourceInstance = this@EnvProxy
        }
    }

    fun clear() {
        synchronized(overrider) {
            overrider.clear()
        }
    }

    fun set(name: String, value: Any?) {
        synchronized(overrider) {
            overrider[name] = value
        }
    }

    fun unset(name: String) {
        synchronized(overrider) {
            overrider.remove(name)
        }
    }

    fun isOverriden(name: String) = overrider.containsKey(name)

    companion object {
        val Instance = EnvProxy()
    }
}
package codes.spectrum.sources.core.agent

enum class AgentType {
    SAVER,
    LOADER,
    CRAWLER,
    NONE;

    companion object {
        fun getByName(name: String) = values().firstOrNull { it.toString() == name.toUpperCase() } ?: NONE
    }
}
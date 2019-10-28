package codes.spectrum.sources.core.agent

enum class AgentMessageTask {
    CRAWL,
    LOAD,
    SAVE,
    NONE;

    companion object {
        fun getByName(name: String) = values().firstOrNull { it.toString() == name.toUpperCase() } ?: NONE
    }
}
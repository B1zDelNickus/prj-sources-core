package codes.spectrum.sources.core.client.b2bdocutils

data class B2bSourceHeaderDoc(
        var prefix: String = BLOCK_PREFIX,
        var name: String = ""
){

    val isNotEmpty get() = prefix.isNotBlank() && name.isNotBlank()

    companion object {
        const val BLOCK_PREFIX = "Блок"
    }

    infix fun B2bSourceHeaderDoc.name(_name: String): B2bSourceHeaderDoc {
        this.name = _name
        return this
    }

    infix fun B2bSourceHeaderDoc.prefix(_prefix: String) {
        this.prefix = _prefix
    }
}
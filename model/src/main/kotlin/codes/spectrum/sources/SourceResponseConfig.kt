package codes.spectrum.sources

/**
 * Конфигурация HTTP-ответа
 */
class SourceResponseConfig(
    /**
     * contentType, котороый будет установлен отдаваемому потоку
     */
    val contentType: String = "application/json",
    /**
     * Обрабатывать ответ потоком или вернуть целиком контекст
     */
    val responseType: SourceResponseType = SourceResponseType.DIRECT
) {
    /**
     * Показывает, будет ответ потоком или нет.
     *   - если true, то в ответе будет всё, записанное в outputStream источником
     *   - если false, то в ответе будет передан сам SourceContext
     */
    fun respondAsStream(): Boolean = responseType == SourceResponseType.STREAM
    
    companion object {
        val DEFAULT = SourceResponseConfig()
    }
}

enum class SourceResponseType {
    /**
     * Данные возвращаются объектом
     */
    DIRECT,
    /**
     * Данные возвращаются потоком
     */
    STREAM;
    
    
}
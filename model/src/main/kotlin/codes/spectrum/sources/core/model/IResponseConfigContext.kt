package codes.spectrum.sources.core.model

import codes.spectrum.sources.SourceResponseConfig
import java.io.OutputStream

interface IResponseConfigContext {
    /**
     * Конфигурация ответа. В ней выбирается:
     *  Отдавать ответ потоком или нет
     *  contentType отдаваемого потока
     */
    val responseConfig: SourceResponseConfig
    
    /**
     * Поток, в который надо писать, если необходимо ответить потоком.
     * Будет проброшен из RestSourceAdapter'а автоматически
     */
    fun outputStream(outputStream: OutputStream)
}
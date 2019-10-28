package codes.spectrum.sources

data class DebugMode (
    var throwError:Boolean = false,
    var sourceDelay:Long = 0,
    var serviceDelay:Long = 0,
    /**
     * Запрос заглушки. Источником будет возвращена типовая структура для целей отладки
     */
    var stub: String? = null,
    var startTime: Long = 0,
    var endTime: Long = 0
)

open class SourceQuery<TQuery>(
    var query:TQuery,
    var timeout:Long = 0,
    var debug: DebugMode? = null,
    //by default REST itself is fail safe, but marks result as not 200
    var errorHttpCode : Int = 201,

    /**
     * Только валидация запроса. Сам запрос не выполняется
     */
    var validateOnly: Boolean? = false,

    /**
     * Отключение платных частей запроса
     */
    var skipPayed: Boolean? = false
)
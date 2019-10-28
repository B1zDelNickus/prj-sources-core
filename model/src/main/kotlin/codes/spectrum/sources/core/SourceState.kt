package codes.spectrum.sources.core

@Deprecated(level = DeprecationLevel.WARNING, message = "use codes.spectrum.api.SourceState", replaceWith = ReplaceWith("SourceState", "codes.spectrum.api.SourceState"))
enum class SourceState(
    val severity: Severity,
    val code: Int,
    val recoverable: Boolean,
    val processingOnly: Boolean,
    val isFinal: Boolean
) {
    /**
     * Прошло успешно
     */
    OK(Severity.OK, 200, false, false, true),
    /**
     * В целом успешно, но содержит некритичные ошибки обработки. Например, при загруке миллиона объектов 10% не загрузились по каким-то причинам и это предусмотрено логикой.
     */
    PARTIAL_OK(Severity.HINT, 201, false, false, true),
    /**
     * Для асинхронных запросов - источник обрабатывает запрос, результат будет позже
     * Требует повторного запроса через небольшое время
     */
    PROGRESS(Severity.WARN, 202, false, false, false),
    /**
     * Во входном запросе при формальном анализе запроса заведомо нет данных для обработки данным источником
     */
    SKIP_QUERY(Severity.HINT, 203, false, true, true),
    /**
     * Режим ожидания реального запуска (например ожидание доп. параметра в запросе)
     */
    WAITING(Severity.WARN, 300, false, true, false),
    /**
     * Запрос не удовлетворяет требованиям источника: содержит данные в недопустимом формате, либо пустое обязательное поле.
     * Например, номер гражданского паспорта РФ содержит 9 или 11 цифр вместо 10. Или пустой ИНН при поиске по ИНН.
     */
    BAD_QUERY(Severity.WARN, 400, false, false, true),
    /**
     * Не найдено объектов, удовлетворяющих запросу
     */
    NOT_FOUND(Severity.HINT, 404, false, false, true),
    /**
     * Общяя неизвестная ошибка
     */
    GENERAL_ERROR(Severity.ERROR, 500, false, false, true),
    /**
     * Ошибка во внешних компонентах ТРЕТЬЕЙ стороны
     */
    EXTERNAL_ERROR(Severity.ERROR, 501, false, false, true),
    /**
     * Ошибка в классах провайдера источника
     */
    SOURCE_ERROR(Severity.ERROR, 502, false, false, true),
    /**
     * Ошибка на уровне REST/Consumer источника
     */
    SERVICE_ERROR(Severity.ERROR, 503, false, false, true),
    /**
     * Ошибки внутри уровня client
     */
    CLIENT_ERROR(Severity.ERROR, 504, false, false, true),
    /**
     * Ошибка на уровне b2b-адаптера или аналогичных интеграционных прокладок
     */
    INTEGRATION_ERROR(Severity.ERROR, 505, false, false, true),
    /**
     * Статус не установлен
     */
    NONE(Severity.ERROR, 506, false, false, false),
    /**
     * Общяя неизвестная ошибка, которая возможно не поизойдет при повторном запросе
     */
    RECOVERABLE_GENERAL_ERROR(Severity.ERROR, 507, true, false, false),
    /**
     * Ошибка во внешних компонентах ТРЕТЬЕЙ стороны, которая возможно не поизойдет при повторном запросе
     */
    RECOVERABLE_EXTERNAL_ERROR(Severity.ERROR, 508, true, false, false),
    /**
     * Ошибка в классах провайдера источника, которая возможно не поизойдет при повторном запросе
     */
    RECOVERABLE_SOURCE_ERROR(Severity.ERROR, 509, true, false, false),
    /**
     * Ошибка на уровне REST/Consumer источника, которая возможно не поизойдет при повторном запросе
     */
    RECOVERABLE_SERVICE_ERROR(Severity.ERROR, 510, true, false, false),
    /**
     * Ошибки внутри уровня client, которая возможно не поизойдет при повторном запросе
     */
    RECOVERABLE_CLIENT_ERROR(Severity.ERROR, 511, true, false, false),
    /**
     * Ошибка на уровне b2b-адаптера или аналогичных интеграционных прокладок, которая возможно не поизойдет при повторном запросе
     */
    RECOVERABLE_INTEGRATION_ERROR(Severity.ERROR, 512, true, false, false),
    /**
     * Досрочное прерывание, НЕ по инициативе ЗАКАЗЧИКА ОТЧЕТА - какое-то системное прерывание
     */
    ABORTED(Severity.WARN, 513, false, true, true),
    /**
     * Досрочное прерывание, по инициативе ЗАКАЗЧИКА ОТЧЕТА
     */
    CANCELLED(Severity.WARN, 514, false, true, true),
    /**
     * Время выполнения (если предусматривает SLA или условия запроса - вышло)
     */
    TIME_OUT(Severity.WARN, 602, false, false, true)

}
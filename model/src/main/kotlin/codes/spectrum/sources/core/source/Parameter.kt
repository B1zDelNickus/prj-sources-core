package codes.spectrum.sources.core.source

import java.time.LocalDate

/**
 * Класс параметра
 */
data class Parameter(
    /**
     * Наименование параметра
     */
    val name: String = "",

    /**
     * Описательный заголовок параметра
     */
    val title: String = "",

    /**
     * Индекс параметра (необходим для сортировки)
     */
    val index: Int = 0,

    /**
     * Тип параметра
     */
    val clazz: Class<*>? = null,

    /**
     * Значение параметра
     */
    val value: Any? = null,

    /**
     * HTML InputType
     */
    val inputType: InputType = detectInputType(clazz)
) {
    val normalizedType get(): String =
        inputType.toString().toLowerCase()

    companion object {
        fun detectInputType(clazz: Class<*>?): InputType =
            when {
                clazz == Boolean::class.java -> InputType.CHECKBOX
                clazz == Int::class.java || clazz == Long::class.java -> InputType.NUMBER
                clazz == LocalDate::class.java -> InputType.DATE
                clazz?.isEnum ?: false -> InputType.ENUM
                else -> InputType.TEXT
            }
    }
}
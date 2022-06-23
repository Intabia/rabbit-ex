package com.intabia.rabbitex

/**
 * Класс-перечисление для приоритета логики в цепочке логик [RabbitMQFailureLogic].
 */
enum class RabbitMQFailureLogicPriorityEnum(private val priority: Long) {

    /** Значение для логики, которая должна отработать перед всеми остальными **/
    BEFORE_ALL(0),

    /** Значение для логики ретраев **/
    RETRY(10L),

    /** Значение для логики парковки **/
    PARKING(20L),

    /**
     * Значение для логики, которая должна отработать после предыдущих.
     */
    AFTER_ALL(100L);


    /**
     * Функция для получения приоритета в качестве переменной типа [Long]
     * @return - приоритет в типе [Long]
     */
    fun getPriorityAsLong() = priority
}

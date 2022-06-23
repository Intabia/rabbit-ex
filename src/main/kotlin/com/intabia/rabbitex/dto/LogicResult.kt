package com.intabia.rabbitex.dto

/**
 * Класс, представляющий результат отрабатывания логики.
 */
data class LogicResult(

    /** Нужна ли отправка сообщения в конце отрабатывания логик. **/
    val needToExecuteNextLogic: Boolean,
    /** Роутинг кей куда отправлять. **/
    val sendRoutingKey: String,
    /** Наименование exchange, куда необходимо отправить итоговое сообщение отправлять. **/
    val sendExchange: String,
)
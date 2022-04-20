package com.intabia.rabbitex.dto

/**
 * Класс для конфигурирования отправки сообщений на RabbitMQ.
 */
data class DelayConfig(

    /**
     * Брокер очереди ожиданий.
     */
    val delayExchange: String,

    /**
     * Ключ маршрутизации сообщения.
     */
    val delayRoutingKey: String
)

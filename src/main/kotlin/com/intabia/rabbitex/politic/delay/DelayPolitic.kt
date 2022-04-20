package com.intabia.rabbitex.politic.delay

import org.springframework.amqp.core.Message


/**
 * Интерфейс политики задержки отправки сообщений в очередь.
 */
interface DelayPolitic {

    /**
     * Константы для очереди ретраев.
     *
     * DELAY_HEADER - заголовок, по названию которого exchange понимает, что нужно отправить сообщение с задержкой.
     * NUM_ATTEMPT_HEADER - заголовок, в котором хранится количество попыток повторно отправить сообщение.
     */
    companion object {
        const val DELAY_HEADER = "x-delay"
        const val NUM_ATTEMPT_HEADER = "x-num-attempt"
    }

    /**
     * Метод получения политики задержки отправки сообщений в очередь.
     * @param message - Сообщение от rabbitmq.
     * @param numAttempt - количество отправок данного сообщения в очередь с задержкой.
     * @return - функция, внутри которой выполняется логика политики.
     */
    fun getPolitic(message: Message, numAttempt: Long): () -> Message
}

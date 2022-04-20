package com.intabia.rabbitex.politic.delay

import org.springframework.amqp.core.Message
import com.intabia.rabbitex.politic.delay.DelayPolitic.Companion.DELAY_HEADER
import com.intabia.rabbitex.politic.delay.DelayPolitic.Companion.NUM_ATTEMPT_HEADER

/**
 * Политика постоянной задержки отправки сообщений в очередь.
 */
class ConstantDelayPolitic : DelayPolitic {

    /**
     * Константы для очереди ретраев.
     *
     * RETRY_DELAY - изначальная задержка в миллисекундах.
     */
    companion object {
        const val RETRY_DELAY: Long = 5000
    }

    /**
     * Метод получения политики задержки отправки сообщений в очередь.
     * @param message - Сообщение от rabbitmq.
     * @param numAttempt - количество отправок данного сообщения в очередь с задержкой.
     * @return - функция, внутри которой выполняется логика политики.
     */
    override fun getPolitic(message: Message, numAttempt: Long): () -> Message {
        return {
            message.messageProperties.headers[DELAY_HEADER] = RETRY_DELAY
            message.messageProperties.headers[NUM_ATTEMPT_HEADER] = numAttempt + 1
            message
        }
    }
}

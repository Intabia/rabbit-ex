package com.intabia.rabbitex.politic.delay

import org.springframework.amqp.core.Message
import com.intabia.rabbitex.politic.delay.DelayPolitic.Companion.DELAY_HEADER
import com.intabia.rabbitex.politic.delay.DelayPolitic.Companion.NUM_ATTEMPT_HEADER

/**
 * Политика увеличения задержки отправки сообщений в очередь.
 */
class IncreasingDelayPolitic : DelayPolitic {

    companion object {
        /**
         * RETRY_DELAY - изначальная задержка в миллисекундах, умножается на количество ретраев при отправлении.
         */
        const val RETRY_DELAY: Long = 5000

        /**
         * RETRY_MAX_DELAY - максимальная задержка в миллисекундах.
         */
        const val RETRY_MAX_DELAY: Long = 15000
    }

    /**
     * Метод получения политики задержки отправки сообщений в очередь.
     * @param message - Сообщение от rabbitmq.
     * @param numAttempt - количество отправок данного сообщения в очередь с задержкой.
     * @return - функция, внутри которой выполняется логика политики.
     */
    override fun getPolitic(message: Message, numAttempt: Long): () -> Message {
        return {
            message.messageProperties.headers[DELAY_HEADER] =
                if (numAttempt * RETRY_DELAY > RETRY_MAX_DELAY) RETRY_MAX_DELAY
                else numAttempt * RETRY_DELAY
            message.messageProperties.headers[NUM_ATTEMPT_HEADER] = numAttempt + 1
            message
        }
    }
}

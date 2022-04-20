package com.intabia.rabbitex.politic.delay

import org.springframework.amqp.core.Message
import org.springframework.beans.factory.annotation.Value

/**
 * Политика увеличения задержки отправки сообщений в очередь принимающая значение задержки из файла конфигурации.
 */
class CustomIncreasingDelayPolitic : DelayPolitic {

    companion object {
        /**
         * RETRY_DELAY - изначальная задержка в миллисекундах, умножается на количество ретраев при отправлении.
         */
        @Value("\${politic.custom.delay}")
        const val RETRY_DELAY: Long = 5000

        /**
         * RETRY_MAX_DELAY - максимальная задержка в миллисекундах.
         */
        @Value("\${politic.custom.max-delay}")
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
            message.messageProperties.headers[DelayPolitic.DELAY_HEADER] =
                if (numAttempt * RETRY_DELAY > RETRY_MAX_DELAY) RETRY_MAX_DELAY
                else numAttempt * RETRY_DELAY
            message.messageProperties.headers[DelayPolitic.NUM_ATTEMPT_HEADER] = numAttempt + 1
            message
        }
    }
}
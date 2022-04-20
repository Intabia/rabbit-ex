package com.intabia.rabbitex.politic.delay

import org.springframework.amqp.core.Message
import org.springframework.beans.factory.annotation.Value

/**
 * Политика постоянной задержки отправки сообщений в очередь принимающая значение задержки из файла конфигурации.
 */
class CustomConstantDelayPolitic : DelayPolitic {

    @Value("\${politic.custom.delay}")
    var customRetryDelay: Long = 5000


    /**
     * Метод получения политики задержки отправки сообщений в очередь.
     * @param message - Сообщение от rabbitmq.
     * @param numAttempt - количество отправок данного сообщения в очередь с задержкой.
     * @return - функция, внутри которой выполняется логика политики.
     */
    override fun getPolitic(message: Message, numAttempt: Long): () -> Message {
        return {
            message.messageProperties.headers[DelayPolitic.DELAY_HEADER] = customRetryDelay
            message.messageProperties.headers[DelayPolitic.NUM_ATTEMPT_HEADER] = numAttempt + 1
            message
        }
    }
}
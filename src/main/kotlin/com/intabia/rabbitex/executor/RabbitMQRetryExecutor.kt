package com.intabia.rabbitex.executor

import mu.KLogging
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Scope
import org.springframework.messaging.Message
import org.springframework.stereotype.Service
import com.intabia.rabbitex.dto.DelayConfig
import com.intabia.rabbitex.dto.ParkingConfig
import com.intabia.rabbitex.politic.delay.DelayPolitic
import com.intabia.rabbitex.politic.parking.ParkingPolitic

/**
 * Класс для отправки в очередь RabbitMQ с механизмом ретраев.
 */
@Service
@Scope("prototype")
class RabbitMQRetryExecutor(
    private val rabbitTemplate: RabbitTemplate,
    private val delayPolitic: DelayPolitic,
    private val parkingPolitic: ParkingPolitic
) {

    /**
     * NUM_ATTEMPT_HEADER - заголовок, в котором хранится количество попыток повторно отправить сообщение.
     */
    companion object {
        const val NUM_ATTEMPT_HEADER = "x-num-attempt"
        val logger = KLogging().logger
    }

    /**
     * Метод сохранения с выполнением клиентских методов внутри механизма ретраев в случае ошибки, принимающий
     * пользовательские реализации ParkingPolitic и DelayPolitic.
     * @param message - сообщение для отправки в очередь.
     * @param toExecute - пользовательская функция для обработки сообщения.
     * @param delayConfig - Конфигурация RabbitMQ для механизма задержки сообщений.
     * @param parkingConfig - Конфигурация RabbitMQ для механизма парковки сообщений.
     * @param delayPolitic - Пользовательская реализация механизма задержки сообщений перед повторной отправкой.
     * @param parkingPolitic - Пользовательская реализация механизма парковки сообщений после превышения максимального
     * количества попыток повторной отправки сообщения.
     */
    fun executeWithRetries(
        message: Message<*>,
        toExecute: () -> Unit,
        delayConfig: DelayConfig,
        parkingConfig: ParkingConfig,
        delayPolitic: DelayPolitic,
        parkingPolitic: ParkingPolitic
    ) {
        executeWithDelayAndParkingPolitics(
            message,
            toExecute,
            delayConfig,
            parkingConfig,
            delayPolitic,
            parkingPolitic
        )
    }


    /**
     * Метод сохранения с выполнением клиентских методов внутри механизма ретраев в случае ошибки.
     * @param message - сообщение для отправки в очередь.
     * @param toExecute - пользовательская функция для обработки сообщения.
     * @param delayConfig - Конфигурация RabbitMQ для механизма задержки сообщений.
     * @param parkingConfig - Конфигурация RabbitMQ для механизма парковки сообщений.
     */
    fun executeWithRetries(
        message: Message<*>,
        toExecute: () -> Unit,
        delayConfig: DelayConfig,
        parkingConfig: ParkingConfig
    ) {
        executeWithDelayAndParkingPolitics(
            message,
            toExecute,
            delayConfig,
            parkingConfig,
            delayPolitic,
            parkingPolitic
        )
    }

    private fun executeWithDelayAndParkingPolitics(
        message: Message<*>,
        toExecute: () -> Unit,
        delayConfig: DelayConfig,
        parkingConfig: ParkingConfig,
        delayPolitic: DelayPolitic,
        parkingPolitic: ParkingPolitic
    ) {
        val numAttempt = message.headers.getOrDefault(NUM_ATTEMPT_HEADER, 1L) as Long

        runCatching {
            run(toExecute)
        }.onSuccess {
            logger.info("Сообщение успешно обработано")
        }.onFailure {
            logger.info(
                """
                    Не удалось обработать сообщение. Попытка № $numAttempt. Политика задержки отправки сообщений в 
                    очередь: ${delayPolitic.javaClass.simpleName}. Политика механизма парковки сообщений: 
                    ${parkingPolitic.javaClass.simpleName} 
                    Максимальное количество ретраев: ${parkingPolitic.maxRetriesCount}
                    Сообщение ошибки: ${it.message}.
                """.trimIndent()
            )
            val exchange: String
            val routingKey: String

            if (numAttempt >= parkingPolitic.maxRetriesCount) {
                exchange = parkingConfig.parkingExchange
                routingKey = parkingConfig.parkingRoutingKey

                logger.info("Превышено число повторных попыток. Сообщение отправлено на парковку.")
            } else {
                exchange = delayConfig.delayExchange
                routingKey = delayConfig.delayRoutingKey
            }

            rabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                message.payload
            )
            {
                run(delayPolitic.getPolitic(it, numAttempt))
            }
        }
    }
}

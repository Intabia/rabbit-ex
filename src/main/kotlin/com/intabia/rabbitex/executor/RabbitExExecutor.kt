package com.intabia.rabbitex.executor

import mu.KLogging
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component
import com.intabia.rabbitex.dto.LogicResult

/** Класс для отправки в очередь RabbitMQ с механизмом повторов при неуспешных попытках */
@Component
class RabbitExExecutor(
    private val rabbitMQFailureLogicsExecutor: RabbitMQFailureLogicsExecutor,
    private val rabbitTemplate: RabbitTemplate,
) {

    /** NUM_ATTEMPT_HEADER - заголовок, в котором хранится количество попыток повторно отправить сообщение */
    private companion object : KLogging()

    /**
     * Метод сохранения с выполнением клиентских методов внутри механизма повторов в случае ошибки.
     * @param message - сообщение для отправки в очередь.
     * @param toExecute - пользовательская функция для обработки сообщения.
     */
    fun executeWithRetries(
        message: Message<*>,
        exceptionsToIgnore: Array<Class<out Throwable>> = emptyArray(),
        executionId: String = "DEFAULT", //TODO: Написать инструкцию по executionId
        toExecute: () -> Unit,
    ) {
        runCatching {
            run(toExecute)
        }.onSuccess {
            logger.info { "Сообщение успешно обработано" }
        }.onFailure { exception ->
            logger.info {
                """При выполнении метода произошла ошибка:${exception.message}"
                  |. Выполняется логика RabbitEX библиотеки.""".trimMargin()
            }
            if (exception.needToIgnore(exceptionsToIgnore))
                return@onFailure
            MessageHeaderAccessor
                .getMutableAccessor(message)
                .apply { setLeaveMutable(true) }
                .executeFailureLogics(executionId)
                .copyMessageAndAddHeadersFromLogics(message)
                .sendMessage()
        }
    }

    /*
    Контекст класса MessageHeaderAccessor необходим логикам, поскольку те могут читать/записывать заголовки сообщения
    для выполнения своей логики
     */
    private fun MessageHeaderAccessor.executeFailureLogics(
        executionId: String,
    ): Pair<LogicResult, MessageHeaderAccessor> =
        rabbitMQFailureLogicsExecutor.executeFailureLogics(executionId, this) to this


    private fun Pair<LogicResult, MessageHeaderAccessor>.copyMessageAndAddHeadersFromLogics(
        messageToCopy: Message<*>,
    ): Pair<LogicResult, Message<*>> =
        first to MessageBuilder
            .fromMessage(messageToCopy)
            .copyHeaders(second.messageHeaders)
            .build()

    private fun Throwable.needToIgnore(exceptionsToIgnore: Array<Class<out Throwable>>): Boolean =
        exceptionsToIgnore.contains(this::class.java)

    private fun Pair<LogicResult, Message<*>>.sendMessage() {
        rabbitTemplate
            .convertAndSend(first.sendExchange, first.sendRoutingKey, second.payload) { mutableMessage ->
                mutableMessage
                    .also { message ->
                        second.headers.forEach { header ->
                            message.messageProperties.setHeader(header.key, header.value)
                        }
                    }
            }
            .also { logger.info { "Сообщение отправлено по ${first.sendRoutingKey}. Наименование брокера: ${first.sendExchange}" } }
    }
}

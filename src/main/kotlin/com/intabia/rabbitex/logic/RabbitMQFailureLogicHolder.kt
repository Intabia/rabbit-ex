package com.intabia.rabbitex.logic

import org.springframework.messaging.support.MessageHeaderAccessor
import com.intabia.rabbitex.RabbitMQFailureLogicPriorityEnum
import com.intabia.rabbitex.dto.LogicResult

/**
 * Интерфейс для классов, имеющих логику обработки сообщения из RabbitMQ в случае возникновения ошибки.
 */
interface RabbitMQFailureLogicHolder {

    /**
     * Метод, представляющий функцию, содержащую в себе логику обработки ошибки, при обработке сообщения из RabbitMQ.
     * Параметры функции:
     * - messageHeaderAccessor - предоставляет доступ к заголовкам сообщения.
     * - previousLogicResult - результат выполнения предыдущей логики.
     */
    fun logicExecute(): (messageHeaderAccessor: MessageHeaderAccessor, previousLogicResult: LogicResult) -> LogicResult

    /**
     * Получить приоритет логики в цепочке остальных логик.
     * По умолчанию логика отрабатывает после логики [RabbitMQFailureLogicPriorityEnum.RETRY]
     * и [RabbitMQFailureLogicPriorityEnum.PARKING].
     */
    fun getPriority() = RabbitMQFailureLogicPriorityEnum.AFTER_ALL.getPriorityAsLong()

    fun getIdExecution(): String = "DEFAULT"
}

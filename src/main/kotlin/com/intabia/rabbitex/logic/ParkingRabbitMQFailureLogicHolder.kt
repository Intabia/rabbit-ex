package com.intabia.rabbitex.logic

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component
import com.intabia.rabbitex.RabbitMQFailureLogicPriorityEnum
import com.intabia.rabbitex.config.ParkingProperties
import com.intabia.rabbitex.dto.LogicResult


/**
 * Класс, содержащий логику механизма парковки.
 */
@EnableConfigurationProperties(ParkingProperties::class)
@Component
@ConditionalOnProperty(prefix = "rabbitex.parking", name = ["enable"], havingValue = "true", matchIfMissing = false)
class ParkingRabbitMQFailureLogicHolder(
    private val parkingProperties: ParkingProperties,
) : RabbitMQFailureLogicHolder {

    /**
     * Выполнение логики механизма парковки.
     */
    override fun logicExecute(): (message: MessageHeaderAccessor, currentResult: LogicResult) -> LogicResult =
        { _: MessageHeaderAccessor, _: LogicResult ->
            LogicResult(true, parkingProperties.routingKey, parkingProperties.exchange)
        }

    override fun getPriority(): Long = RabbitMQFailureLogicPriorityEnum.PARKING.getPriorityAsLong()

}
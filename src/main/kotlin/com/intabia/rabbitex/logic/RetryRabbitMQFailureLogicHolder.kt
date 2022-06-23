package com.intabia.rabbitex.logic

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component
import com.intabia.rabbitex.RabbitMQFailureLogicPriorityEnum
import com.intabia.rabbitex.config.RetryProperties
import com.intabia.rabbitex.dto.LogicResult

@Component
@EnableConfigurationProperties(RetryProperties::class)
@ConditionalOnProperty(prefix = "rabbitex.retry", name = ["enable"], havingValue = "true", matchIfMissing = false)
class RetryRabbitMQFailureLogicHolder(
    private val retryProperties: RetryProperties,
) : RabbitMQFailureLogicHolder {

    companion object {
        const val NUM_ATTEMPT_HEADER = "x-num-attempt"
        const val DELAY_HEADER = "x-delay"
    }

    /**
     * Выполнение логики механизма ретрая
     */
    override fun logicExecute(): (messageHeaderAccessor: MessageHeaderAccessor, currentResult: LogicResult) -> LogicResult =
        { messageHeaderAccessor: MessageHeaderAccessor, _: LogicResult ->
            messageHeaderAccessor.messageHeaders[DELAY_HEADER] = retryProperties.delayBetweenRetries
            val attempts = messageHeaderAccessor.messageHeaders.getOrDefault(NUM_ATTEMPT_HEADER, 1L) as Long
            if (attempts > retryProperties.maxAttempts) {
                with(retryProperties) { LogicResult(true, routingKeyForRetry, exchangeForRetry) }
            } else {
                messageHeaderAccessor.setHeader(NUM_ATTEMPT_HEADER, attempts + 1)
                with(retryProperties) { LogicResult(false, routingKeyForRetry, exchangeForRetry) }
            }

        }

    override fun getPriority(): Long = RabbitMQFailureLogicPriorityEnum.RETRY.getPriorityAsLong()


}
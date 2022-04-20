package com.intabia.rabbitex.test

import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import mu.KLogging
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHeaders
import com.intabia.rabbitex.dto.DelayConfig
import com.intabia.rabbitex.dto.ParkingConfig
import com.intabia.rabbitex.executor.RabbitMQRetryExecutor

@DisplayName("Unit тесты на RabbitMQRetryExecutor")
class RetryExecutorTest {

    private companion object : KLogging()

    @Test
    @DisplayName("Тест на вызов метода с механизмом повтора отправки сообщения. Ожидается успех.")
    fun executeWithRetriesTest() {

        val rabbitMQRetryExecutor = mockk<RabbitMQRetryExecutor>(relaxed = true)

        val message = object : Message<String> {
            override fun getPayload(): String {
                return "test"
            }

            override fun getHeaders(): MessageHeaders {
                return MessageHeaders(mutableMapOf(Pair("test", "test")) as Map<String, Any>?)
            }

        }

        val function: () -> Unit = {}

        val delayConfig = DelayConfig("test", "test")
        val parkingConfig = ParkingConfig("test", "test")
        rabbitMQRetryExecutor.executeWithRetries(message, function, delayConfig, parkingConfig)
        verify { rabbitMQRetryExecutor.executeWithRetries(message, function, delayConfig, parkingConfig) }
        confirmVerified(rabbitMQRetryExecutor)
    }
}
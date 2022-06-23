package ru.dnx.rabbitmqretrylib.test

import io.mockk.confirmVerified
import io.mockk.mockk
import mu.KLogging
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHeaders
import com.intabia.rabbitex.executor.RabbitExExecutor

@DisplayName("Unit тесты на RabbitMQRetryExecutor")
class RetryExecutorTest {

    private companion object : KLogging()

    @Test
    @DisplayName("Тест на вызов метода с механизмом повтора отправки сообщения. Ожидается успех.")
    fun executeWithRetriesTest() {

        val rabbitExExecutor = mockk<RabbitExExecutor>(relaxed = true)

        val message = object : Message<String> {
            override fun getPayload(): String {
                return "test"
            }

            override fun getHeaders(): MessageHeaders {
                return MessageHeaders(mutableMapOf(Pair("test", "test")) as Map<String, Any>?)
            }

        }

        val function: () -> Unit = {}

//        val delayConfig = DelayConfig("test", "test")
//        val parkingConfig = ParkingConfig("test", "test")
//        rabbitMQRetryExecutor.executeWithRetries(message, function, delayConfig, parkingConfig)
//        verify { rabbitMQRetryExecutor.executeWithRetries(message, function, delayConfig, parkingConfig) }
        confirmVerified(rabbitExExecutor)
    }
}
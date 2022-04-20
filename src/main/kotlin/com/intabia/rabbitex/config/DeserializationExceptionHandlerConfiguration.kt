package com.intabia.rabbitex.config

import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.intabia.rabbitex.handler.deserialization.IgnoringInvalidFieldDeserializationExceptionHandler

@Configuration
class DeserializationExceptionHandlerConfiguration {

    @Bean("deserializationProblemHandler")
    @ConditionalOnProperty(
        prefix = "deserialization-config",
        name = ["ignore-deserialization-exceptions"],
        havingValue = "true",
    )
    fun deserializationProblemHandler(): DeserializationProblemHandler {
        return IgnoringInvalidFieldDeserializationExceptionHandler()
    }
}

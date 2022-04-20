package com.intabia.rabbitex.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Конфигурация мапперов и конвертеров json, необходимых для RabbitMQ
 */
@Configuration
class RabbitCommon {

    /**
     * Конфигурирование маппера json
     */
    @Bean
    fun rabbitObjectMapper(
        @Autowired(required = false) deserializationProblemHandler: DeserializationProblemHandler?,
    ): ObjectMapper {
        return ObjectMapper().apply {
            registerKotlinModule()
            registerModule(JavaTimeModule())
            registerModule(Jdk8Module())
            configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
            configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
            configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            takeIf {
                deserializationProblemHandler != null
            }?.let {
                addHandler(deserializationProblemHandler)
            }
        }
    }

    /**
     * Конфигурирование конвертера
     */
    @Bean
    fun rabbitMessageConverter(@Qualifier("rabbitObjectMapper") rabbitObjectMapper: ObjectMapper): MessageConverter {
        return Jackson2JsonMessageConverter(rabbitObjectMapper, "*")
    }

    @Bean
    fun rabbitTemplateSetMessageConverter(
        @Autowired rabbitTemplate: RabbitTemplate,
        @Autowired rabbitMessageConverter: MessageConverter,
    ): RabbitTemplate {
        return rabbitTemplate.apply {
            messageConverter = rabbitMessageConverter
        }
    }

    /**
     * Конфигурирование фабрики для RabbitListener
     */
    @Bean
    fun rabbitListenerContainerFactoryBindingConverter(
        @Autowired rabbitListenerContainerFactory: SimpleRabbitListenerContainerFactory,
        @Autowired rabbitMessageConverter: MessageConverter,
    ): SimpleRabbitListenerContainerFactory {
        return rabbitListenerContainerFactory.apply {
            setMessageConverter(rabbitMessageConverter)
        }
    }
}

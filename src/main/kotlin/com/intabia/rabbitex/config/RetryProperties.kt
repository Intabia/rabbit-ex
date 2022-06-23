package com.intabia.rabbitex.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "rabbitex.retry.configuration")
@ConstructorBinding
data class RetryProperties(
    val maxAttempts: Long,
    val routingKeyForRetry: String,
    val exchangeForRetry: String,
    val delayBetweenRetries: Long, //TODO предусмотреть возможность самим определять ретрай
)

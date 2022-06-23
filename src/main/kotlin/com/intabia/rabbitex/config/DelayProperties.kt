package com.intabia.rabbitex.config

data class DelayProperties(
    val delay: Long,
    val delayRoutingKey: String,
    val delayExchange: String,
)

package com.intabia.rabbitex.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "rabbitex.parking.configuration")
@ConstructorBinding
data class ParkingProperties(
    val routingKey: String,
    val exchange: String,
)

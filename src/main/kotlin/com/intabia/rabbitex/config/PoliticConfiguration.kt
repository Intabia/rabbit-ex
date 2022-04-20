package com.intabia.rabbitex.config

import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.core.env.Environment
import com.intabia.rabbitex.politic.delay.ConstantDelayPolitic
import com.intabia.rabbitex.politic.delay.CustomConstantDelayPolitic
import com.intabia.rabbitex.politic.delay.CustomIncreasingDelayPolitic
import com.intabia.rabbitex.politic.delay.DelayPolitic
import com.intabia.rabbitex.politic.delay.IncreasingDelayPolitic
import com.intabia.rabbitex.politic.parking.CustomParkingPolitic
import com.intabia.rabbitex.politic.parking.LongParkingPolitic
import com.intabia.rabbitex.politic.parking.ParkingPolitic
import com.intabia.rabbitex.politic.parking.ShortParkingPolitic
import com.intabia.rabbitex.politic.parking.WithoutParkingPolitic


/**
 * Класс конфигурации и создания политик.
 */
@Configuration
@RefreshScope
class PoliticConfiguration {

    /**
     * Функция создания политики retry механизма.
     * @return - Когда politic.delay-name = increase - создается IncreasingDelayPolitic.
     * Иначе - создается ConstantDelayPolitic.
     */
    @Bean
    @RefreshScope
    @Scope("prototype")
    fun delayPolitic(
        environment: Environment
    ): DelayPolitic {
        return when (environment.getProperty("politic.delay-name")) {
            "increase" -> IncreasingDelayPolitic()
            "custom-increase" -> CustomIncreasingDelayPolitic()
            "custom-const" -> CustomConstantDelayPolitic()
            else -> ConstantDelayPolitic()
        }
    }

    /**
     * Функция создания политики механизма парковки.
     * @return - Когда politic.parking-name = short - создается ShortParkingPolitic.
     * Когда long - создаетсяLongParkingPolitic.
     * Иначе - создается WithoutParkingPolitic.
     */
    @Bean
    @RefreshScope
    @Scope("prototype")
    fun parkingPolitic(
        environment: Environment
    ): ParkingPolitic {
        return when (environment.getProperty("politic.parking-name")) {
            "short" -> ShortParkingPolitic()
            "long" -> LongParkingPolitic()
            "custom" -> CustomParkingPolitic()
            else -> WithoutParkingPolitic()
        }
    }
}

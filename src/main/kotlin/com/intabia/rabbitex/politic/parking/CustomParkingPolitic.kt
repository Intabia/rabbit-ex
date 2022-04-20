package com.intabia.rabbitex.politic.parking

import org.springframework.beans.factory.annotation.Value

/**
 * Политика механизма парковки принимающая значение максимального количества ретраев из файла конфигурации.
 */
class CustomParkingPolitic : ParkingPolitic {
    /**
     * Максимальное количество ретраев
     */
    @Value("\${politic.custom.retries}")
    override val maxRetriesCount: Long = 10
}
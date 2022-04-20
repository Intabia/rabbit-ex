package com.intabia.rabbitex.politic.parking

/**
 * Политика механизма парковки с длинным циклом ретраев.
 */
class LongParkingPolitic : ParkingPolitic {
    /**
     * Максимальное количество ретраев
     */
    override val maxRetriesCount = 50L
}
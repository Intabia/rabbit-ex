package com.intabia.rabbitex.politic.parking

/**
 * Политика механизма парковки с коротким циклом ретраев.
 */
class ShortParkingPolitic : ParkingPolitic {
    /**
     * Максимальное количество ретраев
     */
    override val maxRetriesCount = 3L
}
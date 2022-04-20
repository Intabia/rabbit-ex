package com.intabia.rabbitex.politic.parking

/**
 * Политика механизма парковки максимально большим количеством ретраев.
 */
class WithoutParkingPolitic(
    /**
     * Максимальное количество ретраев
     */
    override val maxRetriesCount: Long = Long.MAX_VALUE
) : ParkingPolitic
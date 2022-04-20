package com.intabia.rabbitex.politic.parking

/**
 * Интерфейс политики парковки сообщений.
 */
interface ParkingPolitic {
    /**
     * Максимальное количество ретраев
     */
    val maxRetriesCount: Long
}
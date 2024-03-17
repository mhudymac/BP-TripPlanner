package kmp.shared.data.source

import kmp.shared.infrastructure.local.TripEntity

internal interface TripLocalSource {
    fun getAllTrips(): List<TripEntity>

    fun updateOrInsert(items: List<TripEntity>)

    fun deleteAllTrips()

    fun getTripByName(name: String): TripEntity?

    fun deleteTripByName(name: String): Boolean
}
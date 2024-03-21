package kmp.shared.data.source

import kmp.shared.infrastructure.local.TripEntity
import kotlinx.coroutines.flow.Flow

internal interface TripLocalSource {
    fun getAllTrips(): Flow<List<TripEntity>>

    fun updateOrInsert(items: List<TripEntity>)

    fun deleteAllTrips()

    fun getTripByName(name: String): TripEntity?

    fun deleteTripByName(name: String)
}
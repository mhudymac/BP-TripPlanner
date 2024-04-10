package kmp.shared.data.source

import kmp.shared.infrastructure.local.TripEntity
import kotlinx.coroutines.flow.Flow

internal interface TripLocalSource {
    fun getUncompletedTrips(): Flow<List<TripEntity>>

    fun getCompletedTrips(): Flow<List<TripEntity>>

    fun updateOrInsert(items: List<TripEntity>)

    fun deleteAllTrips()

    fun getTripByName(name: String): Flow<TripEntity?>

    fun deleteTripByName(name: String)

    fun getNearestTrip(): Flow<TripEntity?>
    fun insertWithoutId( item: TripEntity): Long

    fun getTripById(id: Long): Flow<TripEntity?>

    fun deleteTripById(id: Long)
}
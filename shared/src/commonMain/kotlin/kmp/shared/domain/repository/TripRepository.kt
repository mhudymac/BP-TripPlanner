package kmp.shared.domain.repository

import kmp.shared.domain.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    suspend fun getUncompletedTrips(): Flow<List<Trip>>

    suspend fun getCompletedTrips(): Flow<List<Trip>>

    suspend fun getTripById(id: Long): Flow<Trip?>

    suspend fun deleteTripById(id: Long)

    suspend fun deleteAllTrips()

    suspend fun insertOrReplace(trips: List<Trip>)

    suspend fun getNearestTrip(): Flow<List<Trip>>

    suspend fun insertWithoutId(trip: Trip): Long
}
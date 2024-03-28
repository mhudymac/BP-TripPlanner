package kmp.shared.domain.repository

import kmp.shared.domain.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    suspend fun getAllTrips(): Flow<List<Trip>>

    suspend fun getTripByName(name: String): Flow<Trip?>

    suspend fun getTripById(id: Long): Flow<Trip?>

    suspend fun deleteTripById(id: Long)

    suspend fun deleteTripByName(name: String)

    suspend fun deleteAllTrips()

    suspend fun insertOrReplace(trips: List<Trip>)

    suspend fun getNearestTrip(): Flow<Trip?>
    suspend fun insertWithoutId(trip: Trip): Long
}
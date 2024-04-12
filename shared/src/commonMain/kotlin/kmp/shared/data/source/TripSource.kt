package kmp.shared.data.source

import kmp.shared.base.Result
import kmp.shared.infrastructure.local.TripEntity
import kmp.shared.infrastructure.local.TripWithPlaces
import kotlinx.coroutines.flow.Flow

internal interface TripLocalSource {
    fun getUncompletedTrips(): Flow<List<TripEntity>>

    fun getCompletedTrips(): Flow<List<TripEntity>>

    fun updateOrInsert(items: List<TripEntity>): Result<Unit>

    fun deleteAllTrips(): Result<Unit>

    fun getNearestTrip(): Flow<List<TripEntity>>

    fun insertWithoutId( item: TripEntity): Result<Long>

    fun getTripById(id: Long): Flow<List<TripWithPlaces>>

    fun deleteTripById(id: Long): Result<Unit>
}
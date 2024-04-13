package kmp.shared.domain.repository

import kmp.shared.base.Result
import kmp.shared.domain.model.Trip

internal interface DistanceRepository {
    suspend fun saveDistance(fromPlaceId: String, toPlaceId: String, distance: Trip.Distance, tripId: Long): Result<Unit>

    suspend fun getDistance(fromPlaceId: String, toPlaceId: String): Result<Trip.Distance>

    suspend fun getDistancesByTripId(tripId: Long): Result<Map<Pair<String, String>, Trip.Distance>>

    suspend fun deleteDistancesByTripId(tripId: Long): Result<Unit>
}
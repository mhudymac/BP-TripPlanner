package kmp.shared.domain.repository

import kmp.shared.domain.model.Trip

internal interface DistanceRepository {
    suspend fun saveDistance(fromPlaceId: String, toPlaceId: String, distance: Trip.Distance, tripId: Long)

    suspend fun getDistance(fromPlaceId: String, toPlaceId: String): Trip.Distance?

    suspend fun deleteDistancesByTripId(tripId: Long)
}
package kmp.shared.data.source

import kmp.shared.infrastructure.local.DistanceEntity

internal interface DistanceLocalSource {
    suspend fun insertOrReplaceDistance(distance: DistanceEntity)

    suspend fun getDistance(fromPlaceId: String, toPlaceId: String): DistanceEntity?

    suspend fun deleteDistancesByTripId(tripId: Long)
}
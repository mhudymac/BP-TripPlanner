package kmp.shared.data.source

import kmp.shared.infrastructure.local.DistanceEntity
import kmp.shared.base.Result

internal interface DistanceLocalSource {
    suspend fun insertOrReplaceDistance(distance: DistanceEntity): Result<Unit>

    suspend fun getDistance(fromPlaceId: String, toPlaceId: String): Result<DistanceEntity>

    suspend fun deleteDistancesByTripId(tripId: Long): Result<Unit>

    suspend fun getDistancesByTripId(tripId: Long): Result<List<DistanceEntity>>
}
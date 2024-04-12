package kmp.shared.infrastructure.source

import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.data.source.DistanceLocalSource
import kmp.shared.infrastructure.local.DistanceEntity
import kmp.shared.infrastructure.local.DistanceQueries

class DistanceLocalSourceImpl(
    private val queries: DistanceQueries
) : DistanceLocalSource {
    override suspend fun insertOrReplaceDistance(distance: DistanceEntity): Result<Unit> {
        try {
            queries.insertOrReplaceDistance(distance)
        } catch (e: Exception) {
            return Result.Error(ErrorResult(message = e.message, throwable = e))
        }
        return Result.Success(Unit)
    }

    override suspend fun getDistance(fromPlaceId: String, toPlaceId: String): Result<DistanceEntity> {
        return queries.getDistance(fromPlaceId, toPlaceId).executeAsOneOrNull()?.let { Result.Success(it) }
            ?: Result.Error(ErrorResult(message = "Distance not found"))
    }

    override suspend fun deleteDistancesByTripId(tripId: Long): Result<Unit> {
        try {
            queries.deleteByTripId(tripId)
        } catch (e: Exception) {
            return Result.Error(ErrorResult(message = e.message, throwable = e))
        }
        return Result.Success(Unit)
    }
}
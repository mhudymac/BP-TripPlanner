package kmp.shared.infrastructure.source

import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.base.error.domain.TripError
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
            return Result.Error(TripError.SavingDistanceError)
        }
        return Result.Success(Unit)
    }

    override suspend fun getDistance(fromPlaceId: String, toPlaceId: String): Result<DistanceEntity> {
        return queries.getDistance(fromPlaceId, toPlaceId).executeAsOneOrNull()?.let { Result.Success(it) }
            ?: Result.Error(TripError.GettingDistancesError)
    }

    override suspend fun deleteDistancesByTripId(tripId: Long): Result<Unit> {
        try {
            queries.deleteByTripId(tripId)
        } catch (e: Exception) {
            return Result.Error(TripError.DeletingDistanceError)
        }
        return Result.Success(Unit)
    }

    override suspend fun getDistancesByTripId(tripId: Long): Result<List<DistanceEntity>> {
        return queries.getByTripId(tripId).executeAsList().let { if(it.isNotEmpty()) Result.Success(it) else Result.Error(TripError.DeletingDistanceError)}
    }
}
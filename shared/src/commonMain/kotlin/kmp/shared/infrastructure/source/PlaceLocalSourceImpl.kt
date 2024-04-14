package kmp.shared.infrastructure.source

import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.base.error.domain.TripError
import kmp.shared.data.source.PlaceLocalSource
import kmp.shared.infrastructure.local.DistanceEntity
import kmp.shared.infrastructure.local.PlaceEntity
import kmp.shared.infrastructure.local.PlaceQueries

internal class PlaceLocalSourceImpl(
    private val queries: PlaceQueries
) : PlaceLocalSource {
    override suspend fun getById(placeId: String, tripId: Long): List<PlaceEntity> {
        return queries.getById(placeId, tripId).executeAsList()
    }

    override suspend fun insertOrReplace(places: List<PlaceEntity>): Result<Unit> {
        places.forEach {
            try {
                queries.insertOrReplace(it)
            }
            catch (e: Exception) {
                return Result.Error(TripError.SavingPlaceError)
            }
        }
        return Result.Success(Unit)
    }

    override suspend fun deleteById(placeId: String, tripId: Long): Result<Unit> {
        try {
            queries.deleteById(placeId, tripId)
        } catch (e: Exception) {
            return Result.Error(TripError.DeletingPlaceError)
        }
        return Result.Success(Unit)
    }

    override suspend fun deleteByTripId(tripId: Long): Result<Unit> {
        try {
            queries.deleteByTripId(tripId)
        } catch (e: Exception) {
            return Result.Error(TripError.DeletingPlaceError)
        }
        return Result.Success(Unit)
    }

    override suspend fun getPlacesByTripID(tripID: Long): List<PlaceEntity> {
        return queries.getByTripId(tripID).executeAsList()
    }
}
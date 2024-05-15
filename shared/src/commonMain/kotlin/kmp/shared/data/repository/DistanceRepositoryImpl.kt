package kmp.shared.data.repository

import kmp.shared.base.Result
import kmp.shared.base.util.extension.map
import kmp.shared.data.source.DistanceLocalSource
import kmp.shared.domain.model.Distance
import kmp.shared.domain.repository.DistanceRepository
import kmp.shared.infrastructure.local.DistanceEntity

internal class DistanceRepositoryImpl(
    private val localSource: DistanceLocalSource
) : DistanceRepository {
    override suspend fun saveDistance(fromPlaceId: String, toPlaceId: String, distance: Distance, tripId: Long): Result<Unit> {
        return localSource.insertOrReplaceDistance(DistanceEntity(fromPlaceId = fromPlaceId, toPlaceId = toPlaceId, distance = distance.distance, duration = distance.duration, trip_id = tripId))
    }

    override suspend fun getDistance(fromPlaceId: String, toPlaceId: String): Result<Distance> {
        return localSource.getDistance(fromPlaceId, toPlaceId).let {
            it.map { distanceEntity -> Distance(distance = distanceEntity.distance, duration = distanceEntity.duration) }
        }
    }

    override suspend fun getDistancesByTripId(tripId: Long): Result<Map<Pair<String, String>, Distance>> {
        return localSource.getDistancesByTripId(tripId).let { result ->
            result.map { distanceEntities ->
                distanceEntities.flatMap { distanceEntity ->
                    listOf(
                        Pair(
                            distanceEntity.fromPlaceId to distanceEntity.toPlaceId,
                            Distance(
                                distance = distanceEntity.distance,
                                duration = distanceEntity.duration,
                            )
                        ),
                        Pair(
                            distanceEntity.toPlaceId to distanceEntity.fromPlaceId,
                            Distance(
                                distance = distanceEntity.distance,
                                duration = distanceEntity.duration,
                            )
                        )
                    )
                }.associate { it }
            }
        }
    }

    override suspend fun deleteDistancesByTripId(tripId: Long): Result<Unit> {
        return localSource.deleteDistancesByTripId(tripId)
    }
}

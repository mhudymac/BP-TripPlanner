package kmp.shared.infrastructure.source

import kmp.shared.data.source.DistanceLocalSource
import kmp.shared.infrastructure.local.DistanceEntity
import kmp.shared.infrastructure.local.DistanceQueries

class DistanceLocalSourceImpl(
    private val queries: DistanceQueries
) : DistanceLocalSource {
    override suspend fun insertOrReplaceDistance(distance: DistanceEntity) {
        queries.insertOrReplaceDistance(distance)
    }

    override suspend fun getDistance(fromPlaceId: String, toPlaceId: String): DistanceEntity? {
        return queries.getDistance(fromPlaceId, toPlaceId).executeAsOneOrNull()
    }

    override suspend fun deleteDistancesByTripId(tripId: Long) {
        queries.deleteByTripId(tripId)
    }
}
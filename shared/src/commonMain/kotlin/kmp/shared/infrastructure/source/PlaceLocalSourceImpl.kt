package kmp.shared.infrastructure.source

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

    override suspend fun insertOrReplace(places: List<PlaceEntity>) {
        places.forEach { queries.insertOrReplace(it) }
    }

    override suspend fun deleteById(placeId: String, tripId: Long) {
        queries.deleteById(placeId, tripId)
    }

    override suspend fun deleteByTripId(tripId: Long) {
        queries.deleteByTripId(tripId)
    }

    override suspend fun getPlacesByTripID(tripID: Long): List<PlaceEntity> {
        return queries.getByTripId(tripID).executeAsList()
    }
}
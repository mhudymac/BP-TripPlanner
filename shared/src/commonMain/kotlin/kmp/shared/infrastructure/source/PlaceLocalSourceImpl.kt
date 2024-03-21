package kmp.shared.infrastructure.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kmp.shared.data.source.PlaceLocalSource
import kmp.shared.infrastructure.local.PlaceEntity
import kmp.shared.infrastructure.local.PlaceQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

internal class PlaceLocalSourceImpl(
    private val queries: PlaceQueries
) : PlaceLocalSource {
    override suspend fun getByTrip(tripName: String): Flow<List<PlaceEntity>> {
        return queries.getPlacesByTrip(tripName).asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun insertOrReplace(places: List<PlaceEntity>) {
        places.forEach { queries.insertOrReplace(it) }
    }

    override suspend fun deleteByTrip(tripName: String) {
        queries.deletePlacesByTrip(tripName)
    }

    override suspend fun deleteAllPlaces() {
        queries.deleteAll()
    }
}
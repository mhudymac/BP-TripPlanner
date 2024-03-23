package kmp.shared.infrastructure.source

import kmp.shared.data.source.PlaceLocalSource
import kmp.shared.infrastructure.local.PlaceEntity
import kmp.shared.infrastructure.local.PlaceQueries

internal class PlaceLocalSourceImpl(
    private val queries: PlaceQueries
) : PlaceLocalSource {
    override suspend fun getById(id: String): List<PlaceEntity> {
        return queries.getById(id).executeAsList()
    }

    override suspend fun insertOrReplace(places: List<PlaceEntity>) {
        places.forEach { queries.insertOrReplace(it) }
    }

    override suspend fun deleteById(id: String) {
        queries.deleteById(id)
    }

    override suspend fun deleteAllPlaces() {
        queries.deleteAll()
    }
}
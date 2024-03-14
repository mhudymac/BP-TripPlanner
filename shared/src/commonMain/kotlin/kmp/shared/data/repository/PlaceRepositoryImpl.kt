package kmp.shared.data.repository

import kmp.shared.base.util.extension.map
import kmp.shared.data.source.PlaceRemoteSource
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.model.Place
import kmp.shared.extension.asDomain
import kmp.shared.infrastructure.model.PlaceDto
import kmp.shared.base.Result


internal class PlaceRepositoryImpl(
    private val source: PlaceRemoteSource
) : PlaceRepository {
    override suspend fun searchPlaces(query: String): Result<List<Place>> {
        return source.searchPlaces(query).map {
            it.results?.map(PlaceDto::asDomain) ?: emptyList()
        }
    }

    override suspend fun getPlace(id: String): Result<Place> {
        return source.getPlace(id).map(PlaceDto::asDomain)
    }
}
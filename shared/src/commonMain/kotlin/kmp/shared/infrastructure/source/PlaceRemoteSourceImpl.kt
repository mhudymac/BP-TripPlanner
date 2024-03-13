package kmp.shared.infrastructure.source

import kmp.shared.base.Result
import kmp.shared.data.source.PlaceRemoteSource
import kmp.shared.infrastructure.model.PlaceDto
import kmp.shared.infrastructure.remote.PlaceService

internal class PlaceRemoteSourceImpl(private val service: PlaceService) : PlaceRemoteSource {
    override suspend fun searchPlaces(query: String): Result<List<PlaceDto>> =
        service.searchPlaces(query)

    override suspend fun getPlace(id: String): Result<PlaceDto> =
        service.getPlaceDetails(id)
}
package kmp.shared.infrastructure.source

import kmp.shared.base.Result
import kmp.shared.data.source.PlaceRemoteSource
import kmp.shared.infrastructure.model.PhotoResponse
import kmp.shared.infrastructure.model.TextSearchResponse
import kmp.shared.infrastructure.model.PlaceDto
import kmp.shared.infrastructure.remote.PlaceService

internal class PlaceRemoteSourceImpl(private val service: PlaceService) : PlaceRemoteSource {
    override suspend fun searchPlaces(query: String): Result<TextSearchResponse> =
        service.searchPlaces(query)

    override suspend fun getPhoto(photoName: String): Result<PhotoResponse> =
        service.getPhoto(photoName)


    override suspend fun getPlace(id: String): Result<PlaceDto> =
        service.getPlaceDetails(id)
}
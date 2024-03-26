package kmp.shared.data.source

import kmp.shared.infrastructure.model.PlaceDto
import kmp.shared.base.Result
import kmp.shared.infrastructure.local.PlaceEntity
import kmp.shared.infrastructure.model.PhotoResponse
import kmp.shared.infrastructure.model.TextSearchResponse


internal interface PlaceRemoteSource {
    suspend fun searchPlaces(query: String): Result<TextSearchResponse>

    suspend fun searchPlacesWithBias(query: String, lat: Double, lng: Double): Result<TextSearchResponse>

    suspend fun getPhoto(photoName: String): Result<PhotoResponse>

    suspend fun getPlace(id: String): Result<PlaceDto>
}

internal interface PlaceLocalSource {
    suspend fun getById(id: String): List<PlaceEntity>

    suspend fun insertOrReplace(places: List<PlaceEntity>)

    suspend fun deleteById(id: String)

    suspend fun deleteAllPlaces()

    suspend fun getPlacesByTripID(tripID: Long): List<PlaceEntity>
}
package kmp.shared.data.source

import kmp.shared.infrastructure.model.PlaceDto
import kmp.shared.base.Result
import kmp.shared.domain.model.Location
import kmp.shared.infrastructure.local.DistanceEntity
import kmp.shared.infrastructure.local.PlaceEntity
import kmp.shared.infrastructure.model.DistanceMatrixDto
import kmp.shared.infrastructure.model.GeocodingDto
import kmp.shared.infrastructure.model.PhotoResponse
import kmp.shared.infrastructure.model.TextSearchResponse


internal interface PlaceRemoteSource {
    suspend fun searchPlaces(query: String): Result<TextSearchResponse>

    suspend fun searchPlacesWithBias(query: String, location: Location): Result<TextSearchResponse>

    suspend fun getPhoto(photoName: String): Result<PhotoResponse>

    suspend fun getPlace(id: String): Result<PlaceDto>

    suspend fun getPlaceByLocation(location: Location): Result<GeocodingDto>

    suspend fun getDistanceMatrix(places: List<String>): Result<DistanceMatrixDto>
}

internal interface PlaceLocalSource {
    suspend fun getById(placeId: String, tripId: Long): List<PlaceEntity>

    suspend fun insertOrReplace(places: List<PlaceEntity>): Result<Unit>

    suspend fun deleteById(placeId: String, tripId: Long): Result<Unit>

    suspend fun deleteByTripId(tripId: Long): Result<Unit>

    suspend fun getPlacesByTripID(tripID: Long): List<PlaceEntity>
}
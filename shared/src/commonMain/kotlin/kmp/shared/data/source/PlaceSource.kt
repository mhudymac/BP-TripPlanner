package kmp.shared.data.source

import kmp.shared.infrastructure.model.PlaceDto
import kmp.shared.base.Result
import kmp.shared.infrastructure.local.PlaceEntity
import kmp.shared.infrastructure.model.PhotoResponse
import kmp.shared.infrastructure.model.TextSearchResponse
import kotlinx.coroutines.flow.Flow


internal interface PlaceRemoteSource {
    suspend fun searchPlaces(query: String): Result<TextSearchResponse>

    suspend fun getPhoto(photoName: String): Result<PhotoResponse>

    suspend fun getPlace(id: String): Result<PlaceDto>
}

internal interface PlaceLocalSource {
    suspend fun getByTrip(tripName: String): Flow<List<PlaceEntity>>

    suspend fun insertOrReplace(places: List<PlaceEntity>)

    suspend fun deleteByTrip(tripName: String)

    suspend fun deleteAllPlaces()
}
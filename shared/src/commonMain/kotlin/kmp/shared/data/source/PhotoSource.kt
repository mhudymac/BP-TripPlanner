package kmp.shared.data.source

import kmp.shared.infrastructure.local.PhotoEntity
import kotlinx.coroutines.flow.Flow

internal interface PhotoLocalSource {
    suspend fun getPhotosByPlaceID(placeId: String): Flow<List<PhotoEntity>>

    suspend fun insertOrReplacePhotos(placeId: String, tripId: Long, photos: List<String>)

    suspend fun deletePhotosByPlaceID(placeId: String)
}
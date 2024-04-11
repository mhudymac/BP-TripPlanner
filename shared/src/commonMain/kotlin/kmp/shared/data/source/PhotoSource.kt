package kmp.shared.data.source

import kmp.shared.infrastructure.local.PhotoEntity
import kotlinx.coroutines.flow.Flow

internal interface PhotoLocalSource {
    suspend fun getPhotos(placeId: String, tripId: Long): Flow<List<PhotoEntity>>

    suspend fun getPhotosByTrip(tripId: Long): Flow<List<PhotoEntity>>

    suspend fun insertOrReplacePhotos(photo: PhotoEntity)

    suspend fun deletePhotosByTripId(tripId: Long)
}
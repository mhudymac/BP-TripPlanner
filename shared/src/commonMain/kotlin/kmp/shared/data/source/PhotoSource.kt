package kmp.shared.data.source

import kmp.shared.infrastructure.local.PhotoEntity
import kotlinx.coroutines.flow.Flow
import kmp.shared.base.Result

internal interface PhotoLocalSource {
    suspend fun getPhotos(placeId: String, tripId: Long): Flow<List<PhotoEntity>>

    suspend fun getPhotosByTrip(tripId: Long): Flow<List<PhotoEntity>>

    suspend fun insertOrReplacePhotos(photo: PhotoEntity): Result<Unit>

    suspend fun deletePhotosByTripId(tripId: Long): Result<Unit>

    suspend fun deletePhotoByUri(uri: String): Result<Unit>
}
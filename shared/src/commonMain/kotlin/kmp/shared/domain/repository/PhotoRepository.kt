package kmp.shared.domain.repository

import kmp.shared.domain.model.Photo
import kotlinx.coroutines.flow.Flow

internal interface PhotoRepository {

    suspend fun getPhotos(placeId: String, tripId: Long): Flow<List<Photo>>

    suspend fun getPhotosByTrip(tripId: Long): Flow<List<Photo>>

    suspend fun insertPhoto(photo: Photo)

    suspend fun deletePhotoByPlace(placeId: String)
}
package kmp.shared.domain.repository

import kotlinx.coroutines.flow.Flow

internal interface PhotoRepository {

    suspend fun getPhotosByPlace(placeId: String): Flow<List<String>>

    suspend fun insertPhoto(placeId: String, tripId: Long, photo: String)

    suspend fun deletePhotoByPlace(placeId: String)
}
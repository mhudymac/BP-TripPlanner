package kmp.shared.data.repository

import kmp.shared.data.source.PhotoLocalSource
import kmp.shared.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class PhotoRepositoryImpl(
    private val source: PhotoLocalSource
): PhotoRepository {
    override suspend fun getPhotosByPlace(placeId: String) : Flow<List<String>> {
        return source.getPhotosByPlaceID(placeId).map { photos -> photos.map { it.photoUri } }
    }

    override suspend fun insertPhoto(placeId: String, tripId: Long, photo: String) {
        source.insertOrReplacePhotos(placeId, tripId, listOf(photo))
    }

    override suspend fun deletePhotoByPlace(placeId: String) {
        source.deletePhotosByPlaceID(placeId)
    }
}
package kmp.shared.infrastructure.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kmp.shared.data.source.PhotoLocalSource
import kmp.shared.infrastructure.local.PhotoEntity
import kmp.shared.infrastructure.local.PhotosQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class PhotoLocalSourceImpl(
    private val queries: PhotosQueries
): PhotoLocalSource {
    override suspend fun getPhotosByPlaceID(placeId: String): Flow<List<PhotoEntity>> {
        return queries.getPhotosByPlace(placeId).asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun insertOrReplacePhotos(placeId: String, tripId: Long, photos: List<String>) {
        photos.forEach { queries.insertPhoto(placeId, tripId, it) }
    }

    override suspend fun deletePhotosByPlaceID(placeId: String) {
        queries.deletePhotosByPlace(placeId)
    }

}
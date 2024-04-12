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
    override suspend fun getPhotos(placeId: String, tripId: Long): Flow<List<PhotoEntity>> {
        return queries.getPhotos(placeId, tripId).asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun getPhotosByTrip(tripId: Long): Flow<List<PhotoEntity>> {
        return queries.getPhotosByTrip(tripId).asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun insertOrReplacePhotos(photo: PhotoEntity) {
        queries.insertPhotoAsEntity(photo)
    }

    override suspend fun deletePhotosByTripId(tripId: Long) {
        queries.deletePhotosByTrip(tripId)
    }

    override suspend fun deletePhotoByUri(uri: String) {
        queries.deletePhotoByUri(uri)
    }


}
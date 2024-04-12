package kmp.shared.data.repository

import kmp.shared.data.source.PhotoLocalSource
import kmp.shared.domain.model.Photo
import kmp.shared.domain.repository.PhotoRepository
import kmp.shared.extension.asDomain
import kmp.shared.extension.asEntity
import kmp.shared.infrastructure.local.PhotoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class PhotoRepositoryImpl(
    private val source: PhotoLocalSource
): PhotoRepository {
    override suspend fun getPhotos(placeId: String, tripId: Long) : Flow<List<Photo>> {
        return source.getPhotos(placeId, tripId).map { it.map(PhotoEntity::asDomain) }
    }

    override suspend fun getPhotosByTrip(tripId: Long): Flow<List<Photo>> {
        return source.getPhotosByTrip(tripId).map { it.map(PhotoEntity::asDomain) }
    }

    override suspend fun insertPhoto(photo: Photo) {
        source.insertOrReplacePhotos(photo.asEntity)
    }

    override suspend fun deletePhotoByTripId(tripId: Long) {
        source.deletePhotosByTripId(tripId)
    }

    override suspend fun deletePhotoByUri(uri: String) {
        TODO("Not yet implemented")
    }
}
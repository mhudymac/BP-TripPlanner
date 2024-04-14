package kmp.shared.domain.repository

import kmp.shared.base.Result
import kmp.shared.domain.model.Photo
import kotlinx.coroutines.flow.Flow

/**
 * This interface represents a repository for photos.
 */
internal interface PhotoRepository {

    /**
     * This function is used to get photos associated with a specific place id and trip id.
     *
     * @param placeId The id of the place whose photos should be retrieved.
     * @param tripId The id of the trip whose photos should be retrieved.
     * @return A Flow of list of photos.
     */
    suspend fun getPhotos(placeId: String, tripId: Long): Flow<List<Photo>>


    /**
     * This function is used to get photos associated with a specific trip id.
     *
     * @param tripId The id of the trip whose photos should be retrieved.
     * @return A Flow of list of photos.
     */
    suspend fun getPhotosByTrip(tripId: Long): Flow<List<Photo>>

    /**
     * This function is used to insert a photo.
     *
     * @param photo The photo to insert.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun insertPhoto(photo: Photo): Result<Unit>

    /**
     * This function is used to delete photos associated with a specific trip id.
     *
     * @param tripId The id of the trip whose photos should be deleted.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun deletePhotoByTripId(tripId: Long): Result<Unit>

    /**
     * This function is used to delete a photo by its URI.
     *
     * @param uri The URI of the photo to be deleted.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun deletePhotoByUri(uri: String): Result<Unit>
}
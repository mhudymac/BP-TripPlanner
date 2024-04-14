package kmp.shared.data.source

import kmp.shared.infrastructure.local.PhotoEntity
import kotlinx.coroutines.flow.Flow
import kmp.shared.base.Result

/**
 * This interface represents a local data source for photos.
 */
internal interface PhotoLocalSource {
    /**
     * This function is used to get photos associated with a specific place id and trip id from the local data source.
     *
     * @param placeId The id of the place whose photos should be retrieved.
     * @param tripId The id of the trip whose photos should be retrieved.
     * @return A Flow of list of photos.
     */
    suspend fun getPhotos(placeId: String, tripId: Long): Flow<List<PhotoEntity>>

    /**
     * This function is used to get photos associated with a specific trip id from the local data source.
     *
     * @param tripId The id of the trip whose photos should be retrieved.
     * @return A Flow of list of photos.
     */
    suspend fun getPhotosByTrip(tripId: Long): Flow<List<PhotoEntity>>

    /**
     * This function is used to insert or replace a photo in the local data source.
     *
     * @param photo The photo to insert or replace.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun insertOrReplacePhotos(photo: PhotoEntity): Result<Unit>

    /**
     * This function is used to delete photos associated with a specific trip id from the local data source.
     *
     * @param tripId The id of the trip whose photos should be deleted.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun deletePhotosByTripId(tripId: Long): Result<Unit>

    /**
     * This function is used to delete a photo by its URI from the local data source.
     *
     * @param uri The URI of the photo to be deleted.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun deletePhotoByUri(uri: String): Result<Unit>
}
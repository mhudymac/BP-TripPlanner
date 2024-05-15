package kmp.shared.data.source

import kmp.shared.infrastructure.model.PlaceDto
import kmp.shared.base.Result
import kmp.shared.domain.model.Location
import kmp.shared.infrastructure.local.PlaceEntity
import kmp.shared.infrastructure.model.DistanceMatrixDto
import kmp.shared.infrastructure.model.GeocodingDto
import kmp.shared.infrastructure.model.PhotoResponse
import kmp.shared.infrastructure.model.TextSearchResponse


/**
 * This interface represents a remote data source for places.
 */
internal interface PlaceRemoteSource {
    /**
     * This function is used to search places in the remote data source.
     *
     * @param query The query to search for.
     * @return A Result object containing either a TextSearchResponse in case of success or an error.
     */
    suspend fun searchPlaces(query: String): Result<TextSearchResponse>

    /**
     * This function is used to search places in the remote data source with a location bias.
     *
     * @param query The query to search for.
     * @param location The location to bias the search towards.
     * @return A Result object containing either a TextSearchResponse in case of success or an error.
     */
    suspend fun searchPlacesWithBias(query: String, location: Location): Result<TextSearchResponse>

    /**
     * This function is used to get a photo from the remote data source.
     *
     * @param photoName The name of the photo to get.
     * @return A Result object containing either a PhotoResponse in case of success or an error.
     */
    suspend fun getPhoto(photoName: String): Result<PhotoResponse>

    /**
     * This function is used to get a place by its id from the remote data source.
     *
     * @param id The id of the place to get.
     * @return A Result object containing either a PlaceDto in case of success or an error.
     */
    suspend fun getPlace(id: String): Result<PlaceDto>

    /**
     * This function is used to get a place by its location from the remote data source.
     *
     * @param location The location of the place to get.
     * @return A Result object containing either a GeocodingDto in case of success or an error.
     */
    suspend fun getPlaceByLocation(location: Location): Result<GeocodingDto>

    /**
     * This function is used to get a distance matrix from the remote data source.
     *
     * @param origins The list of origins.
     * @param destinations The list of destinations.
     * @return A Result object containing either a DistanceMatrixDto in case of success or an error.
     */
    suspend fun getDistanceMatrix(origins: List<String>, destinations: List<String>): Result<DistanceMatrixDto>
}

/**
 * This interface represents a local data source for places.
 */
internal interface PlaceLocalSource {
    /**
     * This function is used to get a place by its id and trip id from the local data source.
     *
     * @param placeId The id of the place to get.
     * @param tripId The id of the trip to get the place from.
     * @return A list of PlaceEntity objects.
     */
    suspend fun getById(placeId: String, tripId: Long): List<PlaceEntity>

    /**
     * This function is used to insert or replace places in the local data source.
     *
     * @param places The list of places to insert or replace.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun insertOrReplace(places: List<PlaceEntity>): Result<Unit>

    /**
     * This function is used to delete a place by its id and trip id from the local data source.
     *
     * @param placeId The id of the place to delete.
     * @param tripId The id of the trip to delete the place from.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun deleteById(placeId: String, tripId: Long): Result<Unit>

    /**
     * This function is used to delete places by their trip id from the local data source.
     *
     * @param tripId The id of the trip to delete the places from.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun deleteByTripId(tripId: Long): Result<Unit>

    /**
     * This function is used to get places by their trip id from the local data source.
     *
     * @param tripID The id of the trip to get the places from.
     * @return A list of PlaceEntity objects.
     */
    suspend fun getPlacesByTripID(tripID: Long): List<PlaceEntity>
}
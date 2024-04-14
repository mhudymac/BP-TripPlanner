package kmp.shared.domain.repository

import kmp.shared.domain.model.Place
import kmp.shared.base.Result
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Trip


/**
 * This interface represents a repository for places.
 */
internal interface PlaceRepository {

    /**
     * This function is used to search places.
     *
     * @param query The query to search for.
     * @return A Result object containing either a list of places in case of success or an error.
     */
    suspend fun searchPlaces(query: String): Result<List<Place>>

    /**
     * This function is used to search places that are closer to certain location.
     *
     * @param query The query to search for.
     * @param location The location to bias the search towards.
     * @return A Result object containing either a list of places in case of success or an error.
     */
    suspend fun searchPlacesWithBias(query: String, location: Location): Result<List<Place>>

    /**
     * This function is used to get a photo.
     *
     * @param photoName The name of the photo to get.
     * @return A Result object containing either a string representing the photo in case of success or an error.
     */
    suspend fun getPhoto(photoName: String): Result<String>

    /**
     * This function is used to get a place by its id.
     *
     * @param id The id of the place to get.
     * @return A Result object containing either a Place object in case of success or an error.
     */
    suspend fun getPlace(id: String): Result<Place>

    /**
     * This function is used to get a place by its location.
     *
     * @param location The location of the place to get.
     * @return A Result object containing either a Place object in case of success or an error.
     */
    suspend fun getPlaceByLocation(location: Location): Result<Place>

    /**
     * This function is used to get a distance matrix for a list of places.
     *
     * @param places The list of places to get the distance matrix for.
     * @return A Result object containing either a list of triples representing the distance matrix in case of success or an error.
     */
    suspend fun getDistanceMatrix(places: List<String>): Result<List<Triple<String, String, Trip.Distance>>>

    /**
     * This function is used to get a distance matrix for a list of origin places and destination places.
     *
     * @param originPlaces The list of origin places.
     * @param destinationPlaces The list of destination places.
     * @return A Result object containing either a list of triples representing the updated distance matrix in case of success or an error.
     */
    suspend fun updateDistanceMatrix(originPlaces: List<String>, destinationPlaces: List<String>,): Result<List<Triple<String, String, Trip.Distance>>>

    /**
     * This function is used to get places by their id and trip id.
     *
     * @param placeId The id of the places to get.
     * @param tripId The id of the trip to get the places from.
     * @return A list of Place objects.
     */
    suspend fun getPlacesById(placeId: String, tripId: Long): List<Place>

    /**
     * This function is used to delete a place by its id and trip id.
     *
     * @param placeId The id of the place to delete.
     * @param tripId The id of the trip to delete the place from.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun deleteById(placeId: String, tripId: Long): Result<Unit>

    /**
     * This function is used to delete places by their trip id.
     *
     * @param tripId The id of the trip to delete the places from.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun deleteByTripId(tripId: Long): Result<Unit>

    /**
     * This function is used to insert or replace places.
     *
     * @param places The list of places to insert or replace.
     * @param tripId The id of the trip to insert or replace the places for.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun insertOrReplace(places: List<Place>, tripId: Long): Result<Unit>

    /**
     * This function is used to get places by their trip id.
     *
     * @param tripID The id of the trip to get the places from.
     * @return A list of Place objects.
     */
    suspend fun getPlacesByTripID(tripID: Long): List<Place>
}
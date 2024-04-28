package kmp.shared.domain.repository

import kmp.shared.base.Result
import kmp.shared.domain.model.Distance
import kmp.shared.domain.model.Trip

/**
 * This interface represents a repository for distances.
 */
internal interface DistanceRepository {
    /**
     * This function is used to save a distance.
     *
     * @param fromPlaceId The id of the place from which the distance starts.
     * @param toPlaceId The id of the place to which the distance ends.
     * @param distance The distance to save.
     * @param tripId The id of the trip with which the distance is associated.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun saveDistance(fromPlaceId: String, toPlaceId: String, distance: Distance, tripId: Long): Result<Unit>

    /**
     * This function is used to get a distance.
     *
     * @param fromPlaceId The id of the place from which the distance starts.
     * @param toPlaceId The id of the place to which the distance ends.
     * @return A Result object containing either the distance in case of success or an error.
     */
    suspend fun getDistance(fromPlaceId: String, toPlaceId: String): Result<Distance>

    /**
     * This function is used to get distances associated with a specific trip id.
     *
     * @param tripId The id of the trip whose distances should be retrieved.
     * @return A Result object containing either a map of pairs of place ids to distances in case of success or an error.
     */
    suspend fun getDistancesByTripId(tripId: Long): Result<Map<Pair<String, String>, Distance>>

    /**
     * This function is used to delete distances associated with a specific trip id.
     *
     * @param tripId The id of the trip whose distances should be deleted.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun deleteDistancesByTripId(tripId: Long): Result<Unit>
}
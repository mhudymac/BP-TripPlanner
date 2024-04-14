package kmp.shared.data.source

import kmp.shared.infrastructure.local.DistanceEntity
import kmp.shared.base.Result

/**
 * This interface represents a local data source for distances.
 */
internal interface DistanceLocalSource {

    /**
     * This function is used to insert or replace a distance in the local data source.
     *
     * @param distance The distance to insert or replace.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun insertOrReplaceDistance(distance: DistanceEntity): Result<Unit>

    /**
     * This function is used to get a distance from the local data source.
     *
     * @param fromPlaceId The id of the place from which the distance starts.
     * @param toPlaceId The id of the place to which the distance ends.
     * @return A Result object containing either the distance in case of success or an error.
     */
    suspend fun getDistance(fromPlaceId: String, toPlaceId: String): Result<DistanceEntity>

    /**
     * This function is used to delete distances associated with a specific trip id from the local data source.
     *
     * @param tripId The id of the trip whose distances should be deleted.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun deleteDistancesByTripId(tripId: Long): Result<Unit>

    /**
     * This function is used to get distances associated with a specific trip id from the local data source.
     *
     * @param tripId The id of the trip whose distances should be retrieved.
     * @return A Result object containing either the list of distances in case of success or an error.
     */
    suspend fun getDistancesByTripId(tripId: Long): Result<List<DistanceEntity>>
}
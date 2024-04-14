package kmp.shared.domain.repository

import kmp.shared.base.Result
import kmp.shared.domain.model.Trip
import kotlinx.coroutines.flow.Flow

/**
 * This interface represents a repository for trips.
 */
interface TripRepository {
    /**
     * This function is used to get uncompleted trips.
     *
     * @return A Flow of list of uncompleted Trip objects.
     */
    suspend fun getUncompletedTrips(): Flow<List<Trip>>

    /**
     * This function is used to get completed trips.
     *
     * @return A Flow of list of completed Trip objects.
     */
    suspend fun getCompletedTrips(): Flow<List<Trip>>

    /**
     * This function is used to get a trip by its id.
     *
     * @param id The id of the trip to get.
     * @return A Flow of the Trip object associated with the id.
     */
    suspend fun getTripById(id: Long): Flow<Trip?>

    /**
     * This function is used to delete a trip by its id.
     *
     * @param id The id of the trip to delete.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun deleteTripById(id: Long): Result<Unit>

    /**
     * This function is used to delete all trips.
     *
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun deleteAllTrips(): Result<Unit>

    /**
     * This function is used to insert or replace trips.
     *
     * @param trips The list of trips to insert or replace.
     * @return A Result object containing either Unit in case of success or an error.
     */
    suspend fun insertOrReplace(trips: List<Trip>): Result<Unit>

    /**
     * This function is used to get the nearest trip by date.
     *
     * @return A Flow of list of the nearest Trip objects.
     */
    suspend fun getNearestTrip(): Flow<List<Trip>>

    /**
     * This function is used to insert a trip without an id.
     *
     * @param trip The trip to insert.
     * @return A Result object containing either the id of the inserted trip in case of success or an error.
     */
    suspend fun insertWithoutId(trip: Trip): Result<Long>
}
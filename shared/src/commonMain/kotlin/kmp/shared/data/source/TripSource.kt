package kmp.shared.data.source

import kmp.shared.base.Result
import kmp.shared.infrastructure.local.TripEntity
import kmp.shared.infrastructure.local.TripWithPlaces
import kotlinx.coroutines.flow.Flow

/**
 * This interface represents a local data source for trips.
 */
internal interface TripLocalSource {
    /**
     * This function is used to get uncompleted trips from the local data source.
     *
     * @return A Flow of list of uncompleted TripEntity objects.
     */
    fun getUncompletedTrips(): Flow<List<TripEntity>>

    /**
     * This function is used to get completed trips from the local data source.
     *
     * @return A Flow of list of completed TripEntity objects.
     */
    fun getCompletedTrips(): Flow<List<TripEntity>>

    /**
     * This function is used to update or insert trips in the local data source.
     *
     * @param items The list of trips to update or insert.
     * @return A Result object containing either Unit in case of success or an error.
     */
    fun updateOrInsert(items: List<TripEntity>): Result<Unit>

    /**
     * This function is used to delete all trips from the local data source.
     *
     * @return A Result object containing either Unit in case of success or an error.
     */
    fun deleteAllTrips(): Result<Unit>

    /**
     * This function is used to get the nearest trip from the local data source by date.
     *
     * @return A Flow of list of the nearest TripEntity objects.
     */
    fun getNearestTrip(): Flow<List<TripEntity>>

    /**
     * This function is used to insert a trip without an id in the local data source.
     *
     * @param item The trip to insert.
     * @return A Result object containing either the id of the inserted trip in case of success or an error.
     */
    fun insertWithoutId( item: TripEntity): Result<Long>

    /**
     * This function is used to get a trip by its id from the local data source.
     *
     * @param id The id of the trip to get.
     * @return A Flow of list of TripWithPlaces objects associated with the trip.
     */
    fun getTripById(id: Long): Flow<List<TripWithPlaces>>

    /**
     * This function is used to delete a trip by its id from the local data source.
     *
     * @param id The id of the trip to delete.
     * @return A Result object containing either Unit in case of success or an error.
     */
    fun deleteTripById(id: Long): Result<Unit>
}
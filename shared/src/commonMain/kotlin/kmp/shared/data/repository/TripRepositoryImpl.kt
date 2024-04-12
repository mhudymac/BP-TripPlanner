package kmp.shared.data.repository

import kmp.shared.base.Result
import kmp.shared.data.source.TripLocalSource
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.TripRepository
import kmp.shared.extension.asDomain
import kmp.shared.extension.asEntity
import kmp.shared.extension.asPlace
import kmp.shared.extension.asTrip
import kmp.shared.infrastructure.local.TripEntity
import kmp.shared.infrastructure.local.TripWithPlaces
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class TripRepositoryImpl(
    private val source: TripLocalSource
) : TripRepository {

    override suspend fun getUncompletedTrips(): Flow<List<Trip>> {
        return source.getUncompletedTrips().map { it.map(TripEntity::asDomain) }
    }

    override suspend fun getCompletedTrips(): Flow<List<Trip>> {
        return source.getCompletedTrips().map { it.map(TripEntity::asDomain) }
    }

    override suspend fun getTripById(id: Long): Flow<Trip?> {
        return source.getTripById(id).map { tripWithPlaces ->
            val trip = tripWithPlaces.firstOrNull()?.asTrip
            val places = tripWithPlaces.mapNotNull { it.asPlace }

            trip?.copy(
                itinerary = trip.order.mapNotNull { order ->
                    places.firstOrNull { it.id == order }
                }
            )
        }
    }

    override suspend fun deleteTripById(id: Long): Result<Unit> {
        return source.deleteTripById(id)
    }
    override suspend fun deleteAllTrips(): Result<Unit> {
        return source.deleteAllTrips()
    }

    override suspend fun insertOrReplace(trips: List<Trip>): Result<Unit> {
        return source.updateOrInsert(trips.map(Trip::asEntity))
    }

    override suspend fun getNearestTrip(): Flow<List<Trip>> {
        return source.getNearestTrip().map { it.map(TripEntity::asDomain) }
    }

    override suspend fun insertWithoutId(trip: Trip): Result<Long> {
        return source.insertWithoutId(trip.asEntity)
    }

}
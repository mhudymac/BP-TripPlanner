package kmp.shared.data.repository

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
            tripWithPlaces.first().asTrip.copy(
                itinerary = tripWithPlaces.mapNotNull { it.asPlace }
            )
        }
    }

    override suspend fun deleteTripById(id: Long) {
        source.deleteTripById(id)
    }
    override suspend fun deleteAllTrips() {
        source.deleteAllTrips()
    }

    override suspend fun insertOrReplace(trips: List<Trip>) {
        source.updateOrInsert(trips.map(Trip::asEntity))
    }

    override suspend fun getNearestTrip(): Flow<List<Trip>> {
        return source.getNearestTrip().map { it.map(TripEntity::asDomain) }
    }

    override suspend fun insertWithoutId(trip: Trip): Long {
        return source.insertWithoutId(trip.asEntity)
    }

}
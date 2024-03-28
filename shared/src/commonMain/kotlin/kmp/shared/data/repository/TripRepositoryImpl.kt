package kmp.shared.data.repository

import kmp.shared.data.source.TripLocalSource
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.TripRepository
import kmp.shared.extension.asDomain
import kmp.shared.extension.asEntity
import kmp.shared.infrastructure.local.TripEntity
import kmp.shared.system.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

internal class TripRepositoryImpl(
    private val source: TripLocalSource
) : TripRepository {

    override suspend fun getAllTrips(): Flow<List<Trip>> {
        return source.getAllTrips().map { it.map(TripEntity::asDomain) }
    }

    override suspend fun getTripByName(name: String): Flow<Trip?> {
        return source.getTripByName(name).map { it?.asDomain }
    }

    override suspend fun getTripById(id: Long): Flow<Trip?> {
        return source.getTripById(id).map { it?.asDomain }
    }

    override suspend fun deleteTripById(id: Long) {
        source.deleteTripById(id)
    }

    override suspend fun deleteTripByName(name: String) {
        source.deleteTripByName(name)
    }

    override suspend fun deleteAllTrips() {
        source.deleteAllTrips()
    }

    override suspend fun insertOrReplace(trips: List<Trip>) {
        source.updateOrInsert(trips.map(Trip::asEntity))
    }

    override suspend fun getNearestTrip(): Flow<Trip?> {
        return source.getNearestTrip().map { it?.asDomain }
    }

    override suspend fun insertWithoutId(trip: Trip): Long {
        return source.insertWithoutId(trip.asEntity)
    }

}
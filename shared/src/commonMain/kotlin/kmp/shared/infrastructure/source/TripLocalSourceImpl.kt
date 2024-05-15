package kmp.shared.infrastructure.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kmp.shared.base.Result
import kmp.shared.base.error.domain.TripError
import kmp.shared.data.source.TripLocalSource
import kmp.shared.infrastructure.local.TripEntity
import kmp.shared.infrastructure.local.TripQueries
import kmp.shared.infrastructure.local.TripWithPlaces
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class TripLocalSourceImpl(
    private val tripQueries: TripQueries
) : TripLocalSource {

    override fun getTripById(id: Long): Flow<List<TripWithPlaces>> {
        return tripQueries.tripWithPlaces(id).asFlow().mapToList(Dispatchers.IO)
    }
    override fun getNearestTrip(): Flow<List<TripEntity>> {
        val date = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        return tripQueries.getNearestTrip(date).asFlow().mapToList(Dispatchers.IO)
    }
    override fun getUncompletedTrips(): Flow<List<TripEntity>> {
        return tripQueries.getUncompletedTrips().asFlow().mapToList(Dispatchers.IO)
    }

    override fun getCompletedTrips(): Flow<List<TripEntity>> {
        return tripQueries.getCompletedTrips().asFlow().mapToList(Dispatchers.IO)
    }

    override fun updateOrInsert(items: List<TripEntity>): Result<Unit> {
        items.forEach {
            try {
                tripQueries.insertOrReplace(it)
            } catch (e: Exception) {
                return Result.Error(TripError.UpdatingTripError)
            }
        }
        return Result.Success(Unit)
    }

    override fun deleteAllTrips(): Result<Unit> {
        try {
            tripQueries.deleteAll()
        } catch (e: Exception) {
            return Result.Error(TripError.DeletingTripError)
        }

        return Result.Success(Unit)
    }

    override fun insertWithoutId(item: TripEntity): Result<Long> {
        try {
            tripQueries.insertWithoutId(item.name, item.date, item.place_order, item.completed)
        } catch (e: Exception) {
            return Result.Error(TripError.SavingTripError)
        }

        return Result.Success(tripQueries.lastInsertedId().executeAsOne())
    }

    override fun deleteTripById(id: Long): Result<Unit> {
        try {
            tripQueries.deleteById(id)
        } catch (e: Exception) {
            return Result.Error(TripError.DeletingTripError)
        }
        return Result.Success(Unit)
    }
}
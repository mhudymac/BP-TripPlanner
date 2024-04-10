package kmp.shared.infrastructure.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kmp.shared.data.source.TripLocalSource
import kmp.shared.infrastructure.local.TripEntity
import kmp.shared.infrastructure.local.TripQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class TripLocalSourceImpl(
    private val tripQueries: TripQueries
) : TripLocalSource {
    override fun getUncompletedTrips(): Flow<List<TripEntity>> {
        return tripQueries.getUncompletedTrips().asFlow().mapToList(Dispatchers.IO)
    }

    override fun getCompletedTrips(): Flow<List<TripEntity>> {
        return tripQueries.getCompletedTrips().asFlow().mapToList(Dispatchers.IO)
    }

    override fun updateOrInsert(items: List<TripEntity>) {
        items.forEach { tripQueries.insertOrReplace(it) }
    }

    override fun deleteAllTrips() {
        tripQueries.deleteAll()
    }

    override fun getTripByName(name: String): Flow<TripEntity?> {
        return tripQueries.getTripByName(name).asFlow().mapToOneOrNull(Dispatchers.IO)
    }

    override fun deleteTripByName(name: String) {
        tripQueries.delete(name)
    }

    override fun getNearestTrip(): Flow<TripEntity?> {
        val date = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        return tripQueries.getNearestTrip(date).asFlow().mapToOneOrNull(Dispatchers.IO)
    }

    override fun insertWithoutId(item: TripEntity): Long {
        tripQueries.insertWithoutId(item.name, item.date, item.place_order, item.completed)
        return tripQueries.lastInsertedId().executeAsOne()
    }

    override fun getTripById(id: Long): Flow<TripEntity?> {
        return tripQueries.getTripById(id).asFlow().mapToOneOrNull(Dispatchers.IO)
    }

    override fun deleteTripById(id: Long) {
        tripQueries.deleteById(id)
    }
}
package kmp.shared.infrastructure.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kmp.shared.data.source.TripLocalSource
import kmp.shared.infrastructure.local.TripEntity
import kmp.shared.infrastructure.local.TripQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class TripLocalSourceImpl(
    private val queries: TripQueries,
) : TripLocalSource {
    override fun getAllTrips(): Flow<List<TripEntity>> {
        return queries.getAllTrips().asFlow().mapToList(Dispatchers.IO)
    }

    override fun updateOrInsert(items: List<TripEntity>) {
        items.forEach { queries.insertOrReplace(it) }
    }

    override fun deleteAllTrips() {
        queries.deleteAll()
    }

    override fun getTripByName(name: String): TripEntity? {
        return queries.getTrip(name).executeAsOneOrNull()
    }

    override fun deleteTripByName(name: String) {
        queries.delete(name)
    }
}
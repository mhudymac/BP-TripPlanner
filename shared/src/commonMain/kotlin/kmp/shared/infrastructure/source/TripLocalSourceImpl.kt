package kmp.shared.infrastructure.source

import kmp.shared.data.source.TripLocalSource
import kmp.shared.infrastructure.local.TripEntity

class TripLocalSourceImpl : TripLocalSource {
    override fun getAllTrips(): List<TripEntity> {
        TODO("Not yet implemented")
    }

    override fun updateOrInsert(items: List<TripEntity>) {
        TODO("Not yet implemented")
    }

    override fun deleteAllTrips() {
        TODO("Not yet implemented")
    }

    override fun getTripByName(name: String): TripEntity? {
        TODO("Not yet implemented")
    }

    override fun deleteTripByName(name: String): Boolean {
        TODO("Not yet implemented")
    }
}
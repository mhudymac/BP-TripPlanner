package kmp.shared.extension

import kmp.shared.domain.model.Trip
import kmp.shared.infrastructure.local.TripEntity

internal val Trip.asEntity
    get() = TripEntity(
        name,
        date.toString(),
        completed = if(completed) 1 else 0,
    )



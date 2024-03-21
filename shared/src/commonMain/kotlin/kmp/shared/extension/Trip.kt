package kmp.shared.extension

import kmp.shared.domain.model.Trip
import kmp.shared.infrastructure.local.TripEntity
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.datetime.LocalDateTime

internal val Trip.asEntity
    get() = TripEntity(
        name,
        date.toString(),
        completed = if(completed) 1 else 0,
    )


internal val TripEntity.asDomain
    get() = Trip(
        name,
        LocalDateTime.parse(date),
        itinerary = emptyFlow(),
        completed = (completed.toInt() == 1),
    )
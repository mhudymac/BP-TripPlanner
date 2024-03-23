package kmp.shared.extension

import kmp.shared.domain.model.Trip
import kmp.shared.infrastructure.local.TripEntity
import kotlinx.datetime.LocalDate

internal val Trip.asEntity
    get() = TripEntity(
        name,
        date.toString(),
        place_order = itinerary.map { it.id }.joinToString(separator = ",") { it },
        completed = if(completed) 1 else 0,
    )


internal val TripEntity.asDomain
    get() = Trip(
            name,
            LocalDate.parse(date),
            itinerary = emptyList(),
            order = place_order.split(",").toList(),
            completed = (completed.toInt() == 1),
            );

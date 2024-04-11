package kmp.shared.extension

import kmp.shared.domain.model.Trip
import kmp.shared.infrastructure.local.TripEntity
import kmp.shared.infrastructure.local.TripWithPlaces
import kotlinx.datetime.LocalDate

internal val Trip.asEntity
    get() = TripEntity(
        id = id,
        name = name,
        date = date.toString(),
        place_order = if(order.isEmpty()) itinerary.map { it.id }.joinToString(separator = ",") { it } else order.joinToString(separator = ",") { it },
        completed = if(completed) 1 else 0,
    )


internal val TripEntity.asDomain
    get() = Trip(
            id = id,
            name = name,
            date = LocalDate.parse(date),
            itinerary = emptyList(),
            order = place_order.split(",").toList(),
            completed = (completed.toInt() == 1),
            )

internal val TripWithPlaces.asTrip
    get() = Trip(
            id = id,
            name = name,
            date = LocalDate.parse(date),
            itinerary = emptyList(),
            order = place_order.split(",").toList(),
            completed = (completed.toInt() == 1),
            )
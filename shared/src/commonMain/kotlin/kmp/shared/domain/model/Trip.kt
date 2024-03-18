package kmp.shared.domain.model

import kotlinx.datetime.LocalDateTime

data class Trip(
    val name: String,
    val date: LocalDateTime,
    val start: Place,
    val itinerary: List<Place>,
    val completed: Boolean = false,
)
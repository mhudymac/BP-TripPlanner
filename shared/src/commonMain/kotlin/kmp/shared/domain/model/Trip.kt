package kmp.shared.domain.model

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
data class Trip(
    val name: String,
    val date: LocalDateTime,
    val itinerary: Flow<List<Place>>,
    val completed: Boolean = false,
)
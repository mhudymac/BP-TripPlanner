package kmp.shared.domain.model

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
data class Trip(
    val id: Long,
    val name: String,
    val date: LocalDate,
    val itinerary: List<Place>,
    val order: List<String>,
    val completed: Boolean = false,
)


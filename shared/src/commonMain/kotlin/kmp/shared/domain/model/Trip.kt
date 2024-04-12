package kmp.shared.domain.model

import kotlinx.datetime.LocalDate

data class Trip(
    val id: Long,
    val name: String,
    val date: LocalDate,
    val itinerary: List<Place>,
    val order: List<String>,
    val completed: Boolean = false,
    var activePlace: String = "",
    var photos: List<Photo> = emptyList(),
    val distances: Map<Pair<String, String>, Distance> = emptyMap(),
){
    data class Distance(
        val distance: Long,
        val duration: Long
    )
}


package kmp.shared.domain.model

data class Place (
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val address: String,
    val photo: String?
)
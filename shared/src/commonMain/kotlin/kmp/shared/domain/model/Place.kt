package kmp.shared.domain.model

data class Place (
    val name: String,
    val id: String,
    val formattedAddress: String,
    val latitude: Double,
    val longitude: Double,
    val googleMapsUri: String,
    val photoName: String?,
    val photoUri: String? = null
)
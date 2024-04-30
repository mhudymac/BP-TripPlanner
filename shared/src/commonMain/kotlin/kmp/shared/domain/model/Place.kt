package kmp.shared.domain.model

data class Place (
    val name: String,
    val id: String,
    val formattedAddress: String,
    val location: Location,
    val googleMapsUri: String,
    val photoId: String?,
    val photoUri: String? = null,
)
package kmp.shared.infrastructure.model

import kotlinx.serialization.Serializable

@Serializable
internal data class PlaceDto(
    val place_id: String,
    val name: String,
    val formatted_address: String,
    val geometry: Geometry,
    val photos: List<Photo>?
) {
    @Serializable
    internal data class Geometry(
        val location: Location
    )

    @Serializable
    internal data class Location(
        val lat: Double,
        val lng: Double
    )

    @Serializable
    internal data class Photo(
        val photo_reference: String
    )
}

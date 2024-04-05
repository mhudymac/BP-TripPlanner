package kmp.shared.infrastructure.model

import kotlinx.serialization.Serializable

@Serializable
internal data class GeocodingDto(
    val results: List<GeoLocations>
)

@Serializable
internal data class GeoLocations(
    val place_id: String,
)
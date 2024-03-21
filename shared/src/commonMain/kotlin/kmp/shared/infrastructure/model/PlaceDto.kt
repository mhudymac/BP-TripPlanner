package kmp.shared.infrastructure.model

import kotlinx.serialization.Serializable

@Serializable
internal data class PhotoResponse(
    val photoUri: String
)

@Serializable
internal data class TextSearchResponse(
    val places: Array<PlaceDto>?
)
@Serializable
internal data class PlaceDto(
    val id: String,
    val displayName: LocalizedText,
    val formattedAddress: String,
    val location: LatLng,
    val googleMapsUri: String,
    val photos: Array<Photo>? = null
) {
    @Serializable
    internal data class LocalizedText(
        val text: String
    )
    @Serializable
    internal data class LatLng(
        val latitude: Double,
        val longitude: Double
    )

    @Serializable
    internal data class Photo(
        val name: String
    )
}

@Serializable
data class TextSearchRequestBody(
    val textQuery: String,
    val maxResultCount: Int
)

internal fun searchFieldMask(): String {
    val fieldNames = listOf(
        "places.displayName",
        "places.id",
        "places.formattedAddress",
        "places.location",
        "places.googleMapsUri",
        "places.photos"
    )
    return fieldNames.joinToString(",")
}
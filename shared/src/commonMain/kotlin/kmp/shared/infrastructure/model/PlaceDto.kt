package kmp.shared.infrastructure.model

import kotlinx.serialization.Serializable

@Serializable
internal data class PhotoResponse(
    val photoUri: String
)

@Serializable
internal data class TextSearchResponse(
    val places: List<PlaceDto>?
)
@Serializable
internal data class PlaceDto(
    val id: String,
    val displayName: LocalizedText,
    val formattedAddress: String,
    val location: LatLng,
    val googleMapsUri: String,
    val photos: List<Photo>? = null
) {
    @Serializable
    data class LocalizedText(
        val text: String
    )

    @Serializable
    data class Photo(
        val name: String
    )
}

@Serializable
internal data class LatLng(
    val latitude: Double,
    val longitude: Double
)

@Serializable
internal data class TextSearchRequestBody(
    val textQuery: String,
    val maxResultCount: Int,
    val locationBias: LocationBias? = null
){
    constructor(textQuery: String, maxResultCount: Int, latitude: Double, longitude: Double, radius: Int)
        : this(textQuery, maxResultCount, LocationBias(Circle(LatLng(latitude, longitude), radius)))
    @Serializable
    data class LocationBias(
        val circle: Circle
    )

    @Serializable
    data class Circle(
        val center: LatLng,
        val radius: Int
    )
}

/**
 * Returns a mask for searching
 */
internal fun searchFieldMask(param: String = "places."): String {
    val fieldNames = listOf(
        param + "displayName",
        param + "id",
        param + "formattedAddress",
        param + "location",
        param + "googleMapsUri",
        param + "photos"
    )
    return fieldNames.joinToString(",")
}
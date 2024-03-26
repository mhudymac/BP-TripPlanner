package kmp.shared.extension

import kmp.shared.domain.model.Place
import kmp.shared.infrastructure.local.PlaceEntity
import kmp.shared.infrastructure.model.PlaceDto

internal val PlaceDto.asDomain
    get() = Place(
        name = displayName.text,
        id = id,
        formattedAddress = formattedAddress,
        latitude = location.latitude,
        longitude = location.longitude,
        googleMapsUri = googleMapsUri,
        photoName = photos?.firstOrNull()?.name
    )

internal fun Place.asEntity(tripId: Long): PlaceEntity {
    return PlaceEntity(
        name = name,
        id = id,
        formattedAddress = formattedAddress,
        lat = latitude,
        lng = longitude,
        googleMapsUri = googleMapsUri,
        photo = photoName,
        photoUri = photoUri,
        trip_id = tripId
    )
}


internal val PlaceEntity.asDomain
    get() = Place(
        name = name,
        id = id,
        formattedAddress = formattedAddress,
        latitude = lat,
        longitude = lng,
        googleMapsUri = googleMapsUri,
        photoName = photo,
        photoUri = photoUri
    )
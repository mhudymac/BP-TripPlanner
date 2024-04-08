package kmp.shared.extension

import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Place
import kmp.shared.infrastructure.local.PlaceEntity
import kmp.shared.infrastructure.model.PlaceDto

internal val PlaceDto.asDomain
    get() = Place(
        name = displayName.text,
        id = id,
        formattedAddress = formattedAddress,
        location = location.let { Location(it.latitude, it.longitude) },
        googleMapsUri = googleMapsUri,
        photoName = photos?.firstOrNull()?.name
    )

internal fun Place.asEntity(tripId: Long): PlaceEntity {
    return PlaceEntity(
        name = name,
        id = id,
        formattedAddress = formattedAddress,
        lat = location.latitude,
        lng = location.longitude,
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
        location = Location(lat, lng),
        googleMapsUri = googleMapsUri,
        photoName = photo,
        photoUri = photoUri
    )
package kmp.shared.extension

import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Place
import kmp.shared.infrastructure.local.PlaceEntity
import kmp.shared.infrastructure.local.TripWithPlaces
import kmp.shared.infrastructure.model.PlaceDto

internal val PlaceDto.asDomain
    get() = Place(
        name = displayName.text,
        id = id,
        formattedAddress = formattedAddress,
        location = location.let { Location(it.latitude, it.longitude) },
        googleMapsUri = googleMapsUri,
        photoId = photos?.firstOrNull()?.name
    )

internal fun Place.asEntity(tripId: Long): PlaceEntity {
    return PlaceEntity(
        name = name,
        id = id,
        formattedAddress = formattedAddress,
        lat = location.latitude,
        lng = location.longitude,
        googleMapsUri = googleMapsUri,
        photo = photoId,
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
        photoId = photo,
        photoUri = photoUri
    )

internal val TripWithPlaces.asPlace
    get(): Place? {
        return if(name_ == null || id_ == null || formattedAddress == null || lat == null || lng == null || googleMapsUri == null) {
            null
        } else {
            Place(
                name = name_,
                id = id_,
                formattedAddress = formattedAddress,
                location = Location(lat, lng),
                googleMapsUri = googleMapsUri,
                photoId = photo,
                photoUri = photoUri
            )
        }
    }

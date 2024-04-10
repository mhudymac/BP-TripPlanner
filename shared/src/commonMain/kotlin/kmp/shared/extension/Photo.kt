package kmp.shared.extension

import kmp.shared.domain.model.Photo
import kmp.shared.infrastructure.local.PhotoEntity

internal val PhotoEntity.asDomain
    get() = Photo(
        placeId = place_id,
        tripId = trip_id,
        photoUri = photo_uri
    )

internal val Photo.asEntity
    get() = PhotoEntity(
        place_id = placeId,
        trip_id = tripId,
        photo_uri = photoUri
    )
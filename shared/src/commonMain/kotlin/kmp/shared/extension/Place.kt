package kmp.shared.extension

import kmp.shared.domain.model.Place
import kmp.shared.infrastructure.local.PlaceEntity
import kmp.shared.infrastructure.model.PlaceDto

internal val PlaceDto.asDomain
    get() = Place(
        place_id,
        name,
        geometry.location.lat,
        geometry.location.lng,
        formatted_address,
        photos?.firstOrNull()?.photo_reference
    )

internal val Place.asEntity : (tripName: String) -> PlaceEntity
    get() = { tripName ->
        PlaceEntity(
            id,
            name,
            lat,
            lng,
            address,
            photo,
            tripName
        )
    }

internal val PlaceEntity.asDomain
    get() = Place(
        id,
        name,
        lat,
        lng,
        address,
        photo
    )
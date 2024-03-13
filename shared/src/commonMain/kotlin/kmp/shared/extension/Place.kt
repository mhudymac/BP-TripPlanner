package kmp.shared.extension

import kmp.shared.domain.model.Place
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
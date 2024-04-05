package kmp.shared.domain.controller

import kmp.shared.domain.model.Location
import kotlinx.coroutines.flow.Flow

internal expect class LocationController {
    val locationFlow: Flow<Location>
    var lastLocation: Location?
        private set
}

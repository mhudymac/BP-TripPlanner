package kmp.shared.domain.controller

import kmp.shared.domain.model.Location
import kotlinx.coroutines.flow.Flow

internal actual class LocationController {
    actual val locationFlow: Flow<Location>
        get() = TODO("Not yet implemented")
    actual val lastLocation: Location?
        get() = TODO("Not yet implemented")
}
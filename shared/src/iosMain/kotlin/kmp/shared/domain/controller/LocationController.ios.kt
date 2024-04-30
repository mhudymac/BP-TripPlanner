package kmp.shared.domain.controller

import kmp.shared.domain.model.Location
import kotlinx.coroutines.flow.Flow

internal actual class LocationController {
    actual val locationFlow: Flow<Location>
        get() = TODO("Not yet implemented")
    private var lastLocation: Location? = null
        get() = TODO("Not yet implemented")

    /**
     * This function is used to get the current location.
     *
     * @return A nullable Location object representing the current location.
     */
    actual suspend fun getCurrentLocation(): Location? {
        TODO("Not yet implemented")
    }

}
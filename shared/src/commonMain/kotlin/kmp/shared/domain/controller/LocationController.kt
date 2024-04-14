package kmp.shared.domain.controller

import kmp.shared.domain.model.Location
import kotlinx.coroutines.flow.Flow

/**
 * This expect class represents a controller for location-related operations.
 * It provides properties and methods to get the current location and a flow of location updates.
 */
internal expect class LocationController {
    /**
     * This property represents a flow of location updates.
     * It is a Flow of Location objects.
     */
    val locationFlow: Flow<Location>

    /**
     * This function is used to get the current location.
     *
     * @return A nullable Location object representing the current location.
     */
    suspend fun getCurrentLocation(): Location?
}

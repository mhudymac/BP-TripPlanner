package kmp.shared.infrastructure.remote.maps

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kmp.shared.base.Result
import kmp.shared.base.error.util.runCatchingCommonNetworkExceptions
import kmp.shared.domain.model.Location
import kmp.shared.infrastructure.model.DistanceMatrixDto
import kmp.shared.infrastructure.model.GeocodingDto

/**
 * This object contains the paths for the Google Maps API endpoints.
 */
internal object MapsPaths {
    private const val root = "/maps/api"
    const val geocoding = "$root/geocode/json"
    const val routeMatrix = "$root/distancematrix/json"
}

/**
 * This class provides services for interacting with the Google Maps API.
 *
 * @property client The HttpClient to use for making requests to the API.
 */
internal class MapsService(private val client: HttpClient) {

    /**
     * This function gets an address by location from the Google Maps API.
     * It makes a GET request to the geocoding endpoint with the latitude and longitude of the location as parameters.
     *
     * @param location The location to get the address for.
     * @return A Result object containing a GeocodingDto in case of success or an error.
     */
    suspend fun getAddressByLocation(location: Location): Result<GeocodingDto> {
        return runCatchingCommonNetworkExceptions {
            client.get(MapsPaths.geocoding) {
                url {
                    parameters["latlng"] = "${location.latitude},${location.longitude}"
                }
            }.body()
        }
    }

    /**
     * This function gets a distance matrix from the Google Maps API.
     * It makes a GET request to the distance matrix endpoint with the origins and destinations as parameters.
     * The mode parameter is set to "walking".
     *
     * @param origins The origins to get the distance matrix for.
     * @param destinations The destinations to get the distance matrix for.
     * @return A Result object containing a DistanceMatrixDto in case of success or an error.
     */
    suspend fun getDistanceMatrix(origins: List<String>, destinations: List<String>): Result<DistanceMatrixDto> {
        return runCatchingCommonNetworkExceptions {
            client.get(MapsPaths.routeMatrix) {
                url {
                    parameters["origins"] = origins.joinToString("|") { "place_id:$it" }
                    parameters["destinations"] = destinations.joinToString("|") { "place_id:$it" }
                    parameters["mode"] = "walking"
                }
            }.body()
        }
    }
}

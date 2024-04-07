package kmp.shared.infrastructure.remote.geocoding

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kmp.shared.base.Result
import kmp.shared.base.error.util.runCatchingCommonNetworkExceptions
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Place
import kmp.shared.infrastructure.model.DistanceMatrixDto
import kmp.shared.infrastructure.model.GeocodingDto


internal object MapsPaths {
    private const val root = "/maps/api"
    const val geocoding = "$root/geocode/json"
    const val routeMatrix = "$root/distancematrix/json"
}

internal class MapsService(private val client: HttpClient) {
    suspend fun getAddressByLocation(location: Location): Result<GeocodingDto> {
        return runCatchingCommonNetworkExceptions {
            client.get(MapsPaths.geocoding) {
                url {
                    parameters["latlng"] = "${location.latitude},${location.longitude}"
                }
            }.body()
        }
    }

    suspend fun getDistanceMatrix(places: List<String>): Result<DistanceMatrixDto> {
        return runCatchingCommonNetworkExceptions {
            client.get(MapsPaths.routeMatrix) {
                url {
                    parameters["origins"] = places.joinToString("|") { "place_id:$it" }
                    parameters["destinations"] = places.joinToString("|") { "place_id:$it" }
                    parameters["mode"] = "walking"
                }
            }.body()
        }
    }
}

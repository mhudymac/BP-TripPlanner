package kmp.shared.infrastructure.remote.geocoding

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kmp.shared.base.Result
import kmp.shared.base.error.util.runCatchingCommonNetworkExceptions
import kmp.shared.domain.model.Location
import kmp.shared.infrastructure.model.GeocodingDto


internal object GeocodingPaths {
    private const val root = "/maps/api/geocode/json"
    const val address = root
}

internal class GeocodingService(private val client: HttpClient) {
    suspend fun getAddress(location: Location): Result<GeocodingDto> {
        return runCatchingCommonNetworkExceptions {
            client.get(GeocodingPaths.address) {
                url {
                    parameters["latlng"] = "${location.latitude},${location.longitude}"
                }
            }.body()
        }
    }
}
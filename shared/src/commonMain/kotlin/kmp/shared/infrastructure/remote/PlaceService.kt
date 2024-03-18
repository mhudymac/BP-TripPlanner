package kmp.shared.infrastructure.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kmp.shared.base.Result
import kmp.shared.base.error.util.runCatchingCommonNetworkExceptions
import kmp.shared.infrastructure.model.GoogleResponseDto
import kmp.shared.infrastructure.model.PlaceDto

internal object PlacePaths {
    private const val root = "/maps/api/place"
    const val textSearch = "$root/textsearch/json"
    const val details = "$root/details/json"
    const val photo = "/maps/api/place/photo"
}

internal class PlaceService(private val client: HttpClient) {

    suspend fun searchPlaces( query: String ): Result<GoogleResponseDto> {
        return runCatchingCommonNetworkExceptions {
            client.get(PlacePaths.textSearch) {
                url {
                    parameters["query"] = query
                }
            }.body()
        }
    }
    suspend fun getPlaceDetails(placeId: String): Result<PlaceDto> {
        return runCatchingCommonNetworkExceptions {
            client.get(PlacePaths.details) {
                url {
                    parameters["place_id"] = placeId
                }
            }.body()
        }
    }

    suspend fun getPhoto(photoReference: String, maxWidth: Int, maxHeight: Int): Result<ByteArray> {
        return runCatchingCommonNetworkExceptions {
            client.get(PlacePaths.photo) {
                url {
                    parameters["maxwidth"] = maxWidth.toString()
                    parameters["maxheight"] = maxHeight.toString()
                    parameters["photo_reference"] = photoReference
                }
            }.body()
        }
    }
}
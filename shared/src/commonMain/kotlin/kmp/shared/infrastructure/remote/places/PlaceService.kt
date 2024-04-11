package kmp.shared.infrastructure.remote.places

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kmp.shared.base.Result
import kmp.shared.base.error.util.runCatchingCommonNetworkExceptions
import kmp.shared.domain.model.Location
import kmp.shared.infrastructure.model.PhotoResponse
import kmp.shared.infrastructure.model.TextSearchResponse
import kmp.shared.infrastructure.model.PlaceDto
import kmp.shared.infrastructure.model.TextSearchRequestBody
import kmp.shared.infrastructure.model.searchFieldMask

internal object PlacePaths {
    private const val root = "/v1"
    const val textSearch = "$root/places:searchText"
    const val details = "$root/places/"
    val photo : (name: String) -> String
        get() = { name -> "$root/$name/media" }
}

internal class PlaceService(private val client: HttpClient) {

    suspend fun searchPlaces( query: String, maxResultCount: Int = 16, location: Location? = null, radius: Int = 50 ): Result<TextSearchResponse> {
        return runCatchingCommonNetworkExceptions {
            client.post(PlacePaths.textSearch) {
                headers.append("X-Goog-FieldMask", searchFieldMask())
                if(location != null)
                    setBody(TextSearchRequestBody( query, maxResultCount, location.latitude, location.longitude, radius))
                else
                    setBody(TextSearchRequestBody( query, maxResultCount))
            }.body()
        }
    }
    suspend fun getPlaceDetails(placeId: String): Result<PlaceDto> {
        return runCatchingCommonNetworkExceptions {
            client.get(PlacePaths.details + placeId) {
                headers.append("X-Goog-FieldMask", searchFieldMask(""))
            }.body()
        }
    }

    suspend fun getPhoto(photoName: String, maxHeightPx: Int = 500, maxWidthPx: Int = 500): Result<PhotoResponse> {
        return runCatchingCommonNetworkExceptions {
            client.get(PlacePaths.photo(photoName)) {
                url {
                    parameters["maxHeightPx"] = maxHeightPx.toString()
                    parameters["maxWidthPx"] = maxWidthPx.toString()
                    parameters["skipHttpRedirect"] = true.toString()
                }
            }.body()
        }
    }
}
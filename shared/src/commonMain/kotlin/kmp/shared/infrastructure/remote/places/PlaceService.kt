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

/**
 * This object contains the paths for the Google Places API endpoints.
 */
internal object PlacePaths {
    private const val root = "/v1"
    const val textSearch = "$root/places:searchText"
    const val details = "$root/places/"
    val photo : (name: String) -> String
        get() = { name -> "$root/$name/media" }
}

/**
 * This class provides services for interacting with the Google Places API.
 *
 * @property client The HttpClient to use for making requests to the API.
 */
internal class PlaceService(private val client: HttpClient) {

    /**
     * This function searches for places using the Google Places API.
     * It makes a POST request to the text search endpoint with the query, max result count, location, and radius as parameters.
     *
     * @param query The query to search for.
     * @param maxResultCount The maximum number of results to return.
     * @param location The location to search around.
     * @param radius The radius to search within.
     * @return A Result object containing a TextSearchResponse in case of success or an error.
     */
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

    /**
     * This function gets the details of a place from the Google Places API.
     * It makes a GET request to the details endpoint with the place ID as a parameter.
     *
     * @param placeId The ID of the place to get the details for.
     * @return A Result object containing a PlaceDto in case of success or an error.
     */
    suspend fun getPlaceDetails(placeId: String): Result<PlaceDto> {
        return runCatchingCommonNetworkExceptions {
            client.get(PlacePaths.details + placeId) {
                headers.append("X-Goog-FieldMask", searchFieldMask(""))
            }.body()
        }
    }

    /**
     * This function gets a photo from the Google Places API.
     * It makes a GET request to the photo endpoint with the photo name, max height, max width, and skip HTTP redirect as parameters.
     *
     * @param photoName The name of the photo to get.
     * @param maxHeightPx The maximum height of the photo in pixels.
     * @param maxWidthPx The maximum width of the photo in pixels.
     * @return A Result object containing a PhotoResponse in case of success or an error.
     */
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
package kmp.shared.infrastructure.source

import kmp.shared.base.Result
import kmp.shared.data.source.PlaceRemoteSource
import kmp.shared.domain.model.Location
import kmp.shared.infrastructure.model.DistanceMatrixDto
import kmp.shared.infrastructure.model.GeocodingDto
import kmp.shared.infrastructure.model.PhotoResponse
import kmp.shared.infrastructure.model.TextSearchResponse
import kmp.shared.infrastructure.model.PlaceDto
import kmp.shared.infrastructure.remote.geocoding.MapsService
import kmp.shared.infrastructure.remote.places.PlaceService

internal class PlaceRemoteSourceImpl(
    private val service: PlaceService,
    private val mapsService: MapsService
) : PlaceRemoteSource {
    override suspend fun searchPlaces(query: String): Result<TextSearchResponse> =
        service.searchPlaces(query)

    override suspend fun searchPlacesWithBias(query: String, lat: Double, lng: Double, ): Result<TextSearchResponse> =
        service.searchPlaces(query, lat = lat, lng = lng)


    override suspend fun getPhoto(photoName: String): Result<PhotoResponse> =
        service.getPhoto(photoName)


    override suspend fun getPlace(id: String): Result<PlaceDto> =
        service.getPlaceDetails(id)

    override suspend fun getPlaceByLocation(location: Location): Result<GeocodingDto> =
        mapsService.getAddressByLocation(location)

    override suspend fun getDistanceMatrix(places: List<String>): Result<DistanceMatrixDto> =
        mapsService.getDistanceMatrix(places)

}
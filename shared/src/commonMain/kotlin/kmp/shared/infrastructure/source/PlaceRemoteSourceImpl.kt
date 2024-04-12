package kmp.shared.infrastructure.source

import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.data.source.PlaceRemoteSource
import kmp.shared.domain.model.Location
import kmp.shared.infrastructure.model.DistanceMatrixDto
import kmp.shared.infrastructure.model.GeocodingDto
import kmp.shared.infrastructure.model.PhotoResponse
import kmp.shared.infrastructure.model.TextSearchResponse
import kmp.shared.infrastructure.model.PlaceDto
import kmp.shared.infrastructure.remote.maps.MapsService
import kmp.shared.infrastructure.remote.places.PlaceService

internal class PlaceRemoteSourceImpl(
    private val service: PlaceService,
    private val mapsService: MapsService
) : PlaceRemoteSource {
    override suspend fun searchPlaces(query: String): Result<TextSearchResponse> {
        return try{
            service.searchPlaces(query)
        }catch (e: Exception) {
            return Result.Error(ErrorResult(message = e.message, throwable = e))
        }
    }


    override suspend fun searchPlacesWithBias(query: String, location: Location): Result<TextSearchResponse> {
        return try{
            service.searchPlaces(query = query, location = location)
        }catch (e: Exception) {
            return Result.Error(ErrorResult(message = e.message, throwable = e))
        }
    }

    override suspend fun getPhoto(photoName: String): Result<PhotoResponse> {
        return try{
            service.getPhoto(photoName)
        }catch (e: Exception) {
            return Result.Error(ErrorResult(message = e.message, throwable = e))
        }
    }

    override suspend fun getPlace(id: String): Result<PlaceDto> {
        return try{
            service.getPlaceDetails(id)
        }catch (e: Exception) {
            return Result.Error(ErrorResult(message = e.message, throwable = e))
        }
    }

    override suspend fun getPlaceByLocation(location: Location): Result<GeocodingDto> {
        return try{
            mapsService.getAddressByLocation(location)
        }catch (e: Exception) {
            return Result.Error(ErrorResult(message = e.message, throwable = e))
        }
    }

    override suspend fun getDistanceMatrix(places: List<String>): Result<DistanceMatrixDto> {
        return try{
            mapsService.getDistanceMatrix(places)
        }catch (e: Exception) {
            return Result.Error(ErrorResult(message = e.message, throwable = e))
        }
    }
}
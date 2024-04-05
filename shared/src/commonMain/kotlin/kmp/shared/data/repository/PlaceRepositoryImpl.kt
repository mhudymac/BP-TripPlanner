package kmp.shared.data.repository

import kmp.shared.base.ErrorResult
import kmp.shared.base.util.extension.map
import kmp.shared.data.source.PlaceRemoteSource
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.model.Place
import kmp.shared.extension.asDomain
import kmp.shared.infrastructure.model.PlaceDto
import kmp.shared.base.Result
import kmp.shared.base.util.extension.getOrNull
import kmp.shared.data.source.PlaceLocalSource
import kmp.shared.domain.model.Location
import kmp.shared.extension.asEntity
import kmp.shared.infrastructure.model.PhotoResponse


internal class PlaceRepositoryImpl(
    private val localSource: PlaceLocalSource,
    private val remoteSource: PlaceRemoteSource
) : PlaceRepository {
    override suspend fun searchPlaces(query: String): Result<List<Place>> {
        return remoteSource.searchPlaces(query).map {
            it.places?.map(PlaceDto::asDomain) ?: emptyList()
        }
    }

    override suspend fun searchPlacesWithBias(query: String, lat: Double, lng: Double): Result<List<Place>> {
        return remoteSource.searchPlacesWithBias(query, lat, lng).map {
            it.places?.map(PlaceDto::asDomain) ?: emptyList()
        }
    }

    override suspend fun getPhoto(photoName: String): Result<String> {
        return remoteSource.getPhoto(photoName).map(PhotoResponse::photoUri)
    }

    override suspend fun getPlace(id: String): Result<Place> {
        return remoteSource.getPlace(id).map(PlaceDto::asDomain)
    }

    override suspend fun getPlaceByLocation(location: Location): Result<Place> {
        val id = remoteSource.getPlaceByLocation(location).map { it.results.first().place_id }
        return when(id) {
            is Result.Success -> getPlace(id.data)
            is Result.Error -> Result.Error(ErrorResult("No place found."))
        }
    }

    override suspend fun getPlacesById(id: String): List<Place> {
        return localSource.getById(id).map { it.asDomain }
    }

    override suspend fun deleteById(id: String) {
        localSource.deleteById(id)
    }

    override suspend fun insertOrReplace(places: List<Place>, tripId: Long) {
        localSource.insertOrReplace(places.map { it.asEntity(tripId) })
    }


    override suspend fun deleteAllPlaces() {
        localSource.deleteAllPlaces()
    }

    override suspend fun getPlacesByTripID(tripID: Long): List<Place> {
        return localSource.getPlacesByTripID(tripID).map { it.asDomain }
    }

}
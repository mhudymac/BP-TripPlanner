package kmp.shared.data.repository

import kmp.shared.base.util.extension.map
import kmp.shared.data.source.PlaceRemoteSource
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.model.Place
import kmp.shared.extension.asDomain
import kmp.shared.infrastructure.model.PlaceDto
import kmp.shared.base.Result
import kmp.shared.data.source.PlaceLocalSource
import kmp.shared.extension.asEntity
import kmp.shared.infrastructure.model.PhotoResponse
import kmp.shared.system.Log


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
        Log.d("DOBREEE", "ESTE LEEEEEEEEEEEEEEEEEEEEEEEEEEEEPSIE")
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

    override suspend fun getPlacesById(id: String): List<Place> {
        return localSource.getById(id).map { it.asDomain }
    }

    override suspend fun deleteById(id: String) {
        localSource.deleteById(id)
    }

    override suspend fun insertOrReplace(places: List<Place>) {
        localSource.insertOrReplace(places.map { it.asEntity })
    }


    override suspend fun deleteAllPlaces() {
        localSource.deleteAllPlaces()
    }


}
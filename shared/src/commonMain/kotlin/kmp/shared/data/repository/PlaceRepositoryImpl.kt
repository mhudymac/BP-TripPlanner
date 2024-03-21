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
import kmp.shared.infrastructure.local.PlaceEntity
import kmp.shared.infrastructure.model.PhotoResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


internal class PlaceRepositoryImpl(
    private val localSource: PlaceLocalSource,
    private val remoteSource: PlaceRemoteSource
) : PlaceRepository {
    override suspend fun searchPlaces(query: String): Result<List<Place>> {
        return remoteSource.searchPlaces(query).map {
            it.places?.map(PlaceDto::asDomain) ?: emptyList()
        }
    }

    override suspend fun getPhoto(photoName: String): Result<String> {
        return remoteSource.getPhoto(photoName).map(PhotoResponse::photoUri)
    }

    override suspend fun getPlace(id: String): Result<Place> {
        return remoteSource.getPlace(id).map(PlaceDto::asDomain)
    }

    override suspend fun getPlacesByTrip(tripName: String): Flow<List<Place>> {
        return localSource.getByTrip(tripName).map {
            it.map(PlaceEntity::asDomain)
        }
    }

    override suspend fun deleteByTrip(tripName: String) {
        localSource.deleteByTrip(tripName)
    }

    override suspend fun insertOrReplace(places: List<Place>, tripName: String) {
        localSource.insertOrReplace(places.map {
            it.asEntity(tripName)
        })
    }


    override suspend fun deleteAllPlaces() {
        localSource.deleteAllPlaces()
    }


}
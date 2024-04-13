package kmp.shared.data.repository

import kmp.shared.base.ErrorResult
import kmp.shared.base.util.extension.map
import kmp.shared.data.source.PlaceRemoteSource
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.model.Place
import kmp.shared.extension.asDomain
import kmp.shared.infrastructure.model.PlaceDto
import kmp.shared.base.Result
import kmp.shared.data.source.PlaceLocalSource
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Trip
import kmp.shared.extension.asEntity
import kmp.shared.infrastructure.local.DistanceEntity
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

    override suspend fun searchPlacesWithBias(query: String, location: Location): Result<List<Place>> {
        return remoteSource.searchPlacesWithBias(query, location).map {
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

    override suspend fun getDistanceMatrix(places: List<String>): Result<List<Triple<String, String, Trip.Distance>>> {
        val maxPlaces = 10
        val results = mutableListOf<Triple<String, String, Trip.Distance>>()

        for (i in places.indices step maxPlaces) {
            val origins = places.subList(i, minOf(i + maxPlaces, places.size))
            for (j in places.indices step maxPlaces) {
                val destinations = places.subList(j, minOf(j + maxPlaces, places.size))
                val result = remoteSource.getDistanceMatrix(origins = origins, destinations = destinations).map {
                    it.rows.mapIndexed { rowIndex, row ->
                        row.elements.mapIndexed { columnIndex, column ->
                            Triple(origins[rowIndex], destinations[columnIndex], Trip.Distance(
                                distance = column.distance.value,
                                duration = column.duration.value
                            ))
                        }
                    }.flatten()
                }
                when (result) {
                    is Result.Success -> results.addAll(result.data)
                    is Result.Error -> return Result.Error(result.error)
                }
            }
        }

        return Result.Success(results)
    }

    override suspend fun getPlacesById(placeId: String, tripId: Long): List<Place> {
        return localSource.getById(placeId, tripId).map { it.asDomain }
    }

    override suspend fun deleteById(placeId: String, tripId: Long): Result<Unit> {
        return localSource.deleteById(placeId, tripId)
    }

    override suspend fun deleteByTripId(tripId: Long): Result<Unit> {
        return localSource.deleteByTripId(tripId)
    }

    override suspend fun insertOrReplace(places: List<Place>, tripId: Long): Result<Unit> {
        return localSource.insertOrReplace(places.map { it.asEntity(tripId) })
    }

    override suspend fun getPlacesByTripID(tripID: Long): List<Place> {
        return localSource.getPlacesByTripID(tripID).map { it.asDomain }
    }

}
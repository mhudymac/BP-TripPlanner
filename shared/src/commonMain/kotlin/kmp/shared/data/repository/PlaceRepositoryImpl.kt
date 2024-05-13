package kmp.shared.data.repository

import kmp.shared.base.ErrorResult
import kmp.shared.base.util.extension.map
import kmp.shared.data.source.PlaceRemoteSource
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.model.Place
import kmp.shared.extension.asDomain
import kmp.shared.infrastructure.model.PlaceDto
import kmp.shared.base.Result
import kmp.shared.base.error.domain.TripError
import kmp.shared.data.source.PlaceLocalSource
import kmp.shared.domain.model.Distance
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Trip
import kmp.shared.extension.asEntity
import kmp.shared.infrastructure.local.DistanceEntity
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
        return when(val response = remoteSource.getPlaceByLocation(location)){
            is Result.Error -> Result.Error(response.error)
            is Result.Success -> {
                if (response.data.results.isEmpty())
                    Result.Error(TripError.GettingPlaceError)
                else
                     getPlace(response.data.results.first().place_id)
            }
        }
    }

    override suspend fun getDistanceMatrix(places: List<String>): Result<List<Triple<String, String, Distance>>> {
        return updateDistanceMatrix(places, places)
    }

    override suspend fun updateDistanceMatrix(
        originPlaces: List<String>,
        destinationPlaces: List<String>,
    ): Result<List<Triple<String, String, Distance>>> {
        val originsStep = minOf(originPlaces.size, 25)
        if (originsStep == 0) return Result.Error(TripError.GettingDistancesError)
        val destinationsStep = minOf(100/originsStep, destinationPlaces.size)
        val results = mutableListOf<Triple<String, String, Distance>>()

        for (i in originPlaces.indices step originsStep) {
            val origins = originPlaces.subList(i, minOf(i + originsStep, originPlaces.size))
            for (j in destinationPlaces.indices step destinationsStep) {
                val destinations = destinationPlaces.subList(j, minOf(j + destinationsStep, destinationPlaces.size))
                val result = remoteSource.getDistanceMatrix(origins = origins, destinations = destinations).map {
                    it.rows.mapIndexed { rowIndex, row ->
                        row.elements.mapIndexed { columnIndex, column ->
                            Triple(origins[rowIndex], destinations[columnIndex], Distance(
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
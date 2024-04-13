package kmp.shared.domain.repository

import kmp.shared.domain.model.Place
import kmp.shared.base.Result
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Trip


internal interface PlaceRepository {
    suspend fun searchPlaces(query: String): Result<List<Place>>

    suspend fun searchPlacesWithBias(query: String, location: Location): Result<List<Place>>

    suspend fun getPhoto(photoName: String): Result<String>

    suspend fun getPlace(id: String): Result<Place>

    suspend fun getPlaceByLocation(location: Location): Result<Place>

    suspend fun getDistanceMatrix(places: List<String>): Result<List<Triple<String, String, Trip.Distance>>>

    suspend fun getPlacesById(placeId: String, tripId: Long): List<Place>

    suspend fun deleteById(placeId: String, tripId: Long): Result<Unit>

    suspend fun deleteByTripId(tripId: Long): Result<Unit>

    suspend fun insertOrReplace(places: List<Place>, tripId: Long): Result<Unit>

    suspend fun getPlacesByTripID(tripID: Long): List<Place>
}
package kmp.shared.domain.repository

import kmp.shared.domain.model.Place
import kmp.shared.base.Result


internal interface PlaceRepository {
    suspend fun searchPlaces(query: String): Result<List<Place>>

    suspend fun searchPlacesWithBias(query: String, lat: Double, lng: Double): Result<List<Place>>

    suspend fun getPhoto(photoName: String): Result<String>

    suspend fun getPlace(id: String): Result<Place>

    suspend fun getPlacesById(id: String): List<Place>

    suspend fun deleteById(id: String)

    suspend fun insertOrReplace(places: List<Place>, tripId: Long)

    suspend fun deleteAllPlaces()

    suspend fun getPlacesByTripID(tripID: Long): List<Place>
}
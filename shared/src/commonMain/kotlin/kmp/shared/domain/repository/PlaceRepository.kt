package kmp.shared.domain.repository

import kmp.shared.domain.model.Place
import kmp.shared.base.Result
import kmp.shared.infrastructure.model.PhotoResponse
import kotlinx.coroutines.flow.Flow


internal interface PlaceRepository {
    suspend fun searchPlaces(query: String): Result<List<Place>>

    suspend fun getPhoto(photoName: String): Result<String>

    suspend fun getPlace(id: String): Result<Place>

    suspend fun getPlacesByTrip(tripName: String): Flow<List<Place>>

    suspend fun deleteByTrip(tripName: String)

    suspend fun insertOrReplace(places: List<Place>, tripName: String)

    suspend fun deleteAllPlaces()
}
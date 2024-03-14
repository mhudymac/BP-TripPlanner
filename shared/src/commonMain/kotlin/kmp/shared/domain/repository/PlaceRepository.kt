package kmp.shared.domain.repository

import kmp.shared.domain.model.Place
import kmp.shared.base.Result


internal interface PlaceRepository {
    suspend fun searchPlaces(query: String): Result<List<Place>>

    suspend fun getPlace(id: String): Result<Place>
}
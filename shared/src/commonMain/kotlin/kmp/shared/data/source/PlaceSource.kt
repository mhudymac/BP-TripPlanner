package kmp.shared.data.source

import kmp.shared.infrastructure.model.PlaceDto
import kmp.shared.base.Result


internal interface PlaceRemoteSource {
    suspend fun searchPlaces(query: String): Result<List<PlaceDto>>

    suspend fun getPlace(id: String): Result<PlaceDto>
}
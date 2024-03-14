package kmp.shared.data.source

import kmp.shared.infrastructure.model.PlaceDto
import kmp.shared.base.Result
import kmp.shared.infrastructure.model.GoogleResponseDto


internal interface PlaceRemoteSource {
    suspend fun searchPlaces(query: String): Result<GoogleResponseDto>

    suspend fun getPlace(id: String): Result<PlaceDto>
}
package kmp.shared.domain.usecase.place

import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Place
import kmp.shared.domain.repository.PlaceRepository

interface SearchPlacesWithBiasUseCase : UseCaseResult<Triple<String, Double, Double>, List<Place>>

internal class SearchPlacesWithBiasUseCaseImpl(
    private val repository: PlaceRepository,
) : SearchPlacesWithBiasUseCase {
    override suspend fun invoke(params: Triple<String, Double, Double>) = repository.searchPlacesWithBias(
        query = params.first,
        lat = params.second,
        lng = params.third
    )
}
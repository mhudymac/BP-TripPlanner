package kmp.shared.domain.usecase.place

import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Place
import kmp.shared.domain.repository.PlaceRepository

interface SearchPlacesWithBiasUseCase : UseCaseResult<Pair<String, Location>, List<Place>>

internal class SearchPlacesWithBiasUseCaseImpl(
    private val repository: PlaceRepository,
) : SearchPlacesWithBiasUseCase {
    override suspend fun invoke(params: Pair<String, Location>) = repository.searchPlacesWithBias(
        query = params.first,
        location = params.second
    )
}
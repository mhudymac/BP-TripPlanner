package kmp.shared.domain.usecase.place

import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Place
import kmp.shared.domain.repository.PlaceRepository

interface SearchPlacesWithBiasUseCase : UseCaseResult<SearchPlacesWithBiasUseCase.Params, List<Place>> {
    data class Params(val query: String, val location: Location)
}

internal class SearchPlacesWithBiasUseCaseImpl(
    private val repository: PlaceRepository,
) : SearchPlacesWithBiasUseCase {
    override suspend fun invoke(params: SearchPlacesWithBiasUseCase.Params) =
        repository.searchPlacesWithBias(
            query = params.query,
            location = params.location
        )
}
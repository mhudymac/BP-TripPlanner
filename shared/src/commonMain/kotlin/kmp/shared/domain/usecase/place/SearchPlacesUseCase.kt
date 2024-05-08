package kmp.shared.domain.usecase.place

import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Place
import kmp.shared.domain.repository.PlaceRepository

interface SearchPlacesUseCase : UseCaseResult<SearchPlacesUseCase.Params, List<Place>> {
    data class Params(val query: String)
}

internal class SearchPlacesUseCaseImpl(
    private val repository: PlaceRepository,
) : SearchPlacesUseCase {
    override suspend fun invoke(params: SearchPlacesUseCase.Params) = repository.searchPlaces(query = params.query)

}
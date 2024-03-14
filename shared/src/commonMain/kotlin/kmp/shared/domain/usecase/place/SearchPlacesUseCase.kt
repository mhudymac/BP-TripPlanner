package kmp.shared.domain.usecase.place

import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Place
import kmp.shared.domain.repository.PlaceRepository

interface SearchPlacesUseCase : UseCaseResult<String, List<Place>>

internal class SearchPlacesUseCaseImpl(
    private val repository: PlaceRepository,
) : SearchPlacesUseCase {
    override suspend fun invoke(params: String) = repository.searchPlaces(query = params)

}
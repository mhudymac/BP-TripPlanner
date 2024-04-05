package kmp.shared.domain.usecase.place

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Place
import kmp.shared.domain.repository.PlaceRepository

interface GetPlaceByLocationUseCase: UseCaseResult<Location, Place>

internal class GetPlaceByLocationUseCaseImpl(
    private val placeRepository: PlaceRepository,
): GetPlaceByLocationUseCase {
    override suspend fun invoke(params: Location): Result<Place> {
        return placeRepository.getPlaceByLocation(params)
    }
}
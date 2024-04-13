package kmp.shared.domain.usecase.place

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.base.usecase.UseCaseResultNoParams
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Place
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.usecase.location.GetLocationUseCase

interface GetPlaceByLocationUseCase: UseCaseResultNoParams<Place>

internal class GetPlaceByLocationUseCaseImpl(
    private val placeRepository: PlaceRepository,
    private val getLocationUseCase: GetLocationUseCase
): GetPlaceByLocationUseCase {
    override suspend fun invoke(): Result<Place> {
        return when(val location = getLocationUseCase()){
            is Result.Success -> {
                placeRepository.getPlaceByLocation(location.data)
            }

            is Result.Error -> Result.Error(location.error)
        }
    }
}
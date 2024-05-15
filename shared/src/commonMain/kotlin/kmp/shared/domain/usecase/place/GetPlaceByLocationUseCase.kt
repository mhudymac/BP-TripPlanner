package kmp.shared.domain.usecase.place

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResultNoParams
import kmp.shared.domain.model.Place
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.usecase.location.GetLocationUseCase

interface GetPlaceByLocationUseCase: UseCaseResultNoParams<Place>

internal class GetPlaceByLocationUseCaseImpl(
    private val placeRepository: PlaceRepository,
    private val getLocationUseCase: GetLocationUseCase
): GetPlaceByLocationUseCase {

    /**
     * It first gets the current location using the GetLocationUseCase.
     * If the location is successfully retrieved, it gets the place by the location from the PlaceRepository.
     * If the location is not successfully retrieved, it returns the error.
     *
     * @return A Result object containing either a Place object in case of success or an error.
     */
    override suspend fun invoke(): Result<Place> {
        return when(val location = getLocationUseCase()){
            is Result.Success -> {
                placeRepository.getPlaceByLocation(location.data)
            }
            is Result.Error -> Result.Error(location.error)
        }
    }
}
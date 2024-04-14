package kmp.shared.domain.usecase.location

import kmp.shared.base.Result
import kmp.shared.base.error.domain.TripError
import kmp.shared.base.usecase.UseCaseResultNoParams
import kmp.shared.domain.controller.LocationController
import kmp.shared.domain.model.Location

interface GetLocationUseCase: UseCaseResultNoParams<Location>

internal class GetLocationUseCaseImpl(
    private val locationController: LocationController,
): GetLocationUseCase {
    override suspend fun invoke(): Result<Location> {
        val location = locationController.getCurrentLocation()
        return if (location != null) {
            Result.Success(location)
        } else {
            Result.Error(TripError.NoLocationError)
        }
    }
}
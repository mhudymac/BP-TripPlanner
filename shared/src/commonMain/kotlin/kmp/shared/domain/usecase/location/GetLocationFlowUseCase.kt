package kmp.shared.domain.usecase.location

import kmp.shared.base.Result
import kmp.shared.base.error.domain.TripError
import kmp.shared.base.usecase.UseCaseFlowResultNoParams
import kmp.shared.domain.controller.LocationController
import kmp.shared.domain.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GetLocationFlowUseCase: UseCaseFlowResultNoParams<Location>

internal class GetLocationFlowUseCaseImpl internal constructor(
    private val locationController: LocationController
) : GetLocationFlowUseCase {
    override suspend fun invoke(): Flow<Result<Location>> = locationController.locationFlow.map {
        it?.let{ Result.Success(it) } ?: Result.Error(TripError.GettingLocationError)
    }
}
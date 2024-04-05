package kmp.shared.domain.usecase.location

import kmp.shared.base.usecase.UseCaseFlowNoParams
import kmp.shared.domain.controller.LocationController
import kmp.shared.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface GetLocationFlowUseCase: UseCaseFlowNoParams<Location>

internal class GetLocationFlowUseCaseImpl internal constructor(
    private val locationController: LocationController
) : GetLocationFlowUseCase {
    override suspend fun invoke(): Flow<Location> = locationController.locationFlow
}
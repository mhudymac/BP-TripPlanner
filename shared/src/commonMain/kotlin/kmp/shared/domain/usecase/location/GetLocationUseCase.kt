package kmp.shared.domain.usecase.location

import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResultNoParams
import kmp.shared.domain.controller.LocationController
import kmp.shared.domain.model.Location
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.withTimeout

interface GetLocationUseCase: UseCaseResultNoParams<Location>

internal class GetLocationUseCaseImpl(
    private val locationController: LocationController,
): GetLocationUseCase {
    override suspend fun invoke(): Result<Location> {

        val location = locationController.getCurrentLocation()
        return if(location != null) Result.Success(location)
        else Result.Error(ErrorResult("Location not found"))
    }
}
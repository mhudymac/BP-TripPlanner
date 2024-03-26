package kmp.shared.domain.usecase.trip

import kmp.shared.base.usecase.UseCaseFlowResult
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.TripRepository
import kmp.shared.base.Result


interface RemoveTripUseCase: UseCaseResult<String, Unit>

internal class RemoveTripUseCaseImpl internal constructor(
    private val tripRepository: TripRepository
) : RemoveTripUseCase {
    override suspend fun invoke(params: String): Result<Unit> {
        tripRepository.deleteTripByName(params)
        return Result.Success(Unit)
    }
}
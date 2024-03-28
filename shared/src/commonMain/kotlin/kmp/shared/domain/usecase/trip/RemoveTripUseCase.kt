package kmp.shared.domain.usecase.trip

import kmp.shared.base.usecase.UseCaseFlowResult
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.TripRepository
import kmp.shared.base.Result


interface RemoveTripUseCase: UseCaseResult<Long, Unit>

internal class RemoveTripUseCaseImpl internal constructor(
    private val tripRepository: TripRepository
) : RemoveTripUseCase {
    override suspend fun invoke(params: Long): Result<Unit> {
        tripRepository.deleteTripById(params)
        return Result.Success(Unit)
    }
}
package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.repository.TripRepository


interface RemoveTripUseCase: UseCaseResult<RemoveTripUseCase.Params, Unit> {
    data class Params(val tripId: Long)
}

internal class RemoveTripUseCaseImpl internal constructor(
    private val tripRepository: TripRepository
) : RemoveTripUseCase {
    override suspend fun invoke(params: RemoveTripUseCase.Params): Result<Unit> = tripRepository.deleteTripById(params.tripId)
}
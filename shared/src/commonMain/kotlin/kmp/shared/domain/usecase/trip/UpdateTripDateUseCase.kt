package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.TripRepository
import kmp.shared.system.Log

interface UpdateTripDateUseCase: UseCaseResult<Trip, Unit>

internal class UpdateOnlyTripDetailsUseCaseImpl(
    private val tripRepository: TripRepository
): UpdateTripDateUseCase {

    override suspend fun invoke(params: Trip): Result<Unit> {
        tripRepository.insertOrReplace(listOf(params))

        return Result.Success(Unit)
    }
}
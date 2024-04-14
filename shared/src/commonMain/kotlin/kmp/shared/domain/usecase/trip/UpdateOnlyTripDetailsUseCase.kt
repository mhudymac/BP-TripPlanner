package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.TripRepository

interface UpdateOnlyTripDetailsUseCase: UseCaseResult<Trip, Unit>

internal class UpdateOnlyOnlyTripDetailsUseCaseImpl(
    private val tripRepository: TripRepository
): UpdateOnlyTripDetailsUseCase {

    override suspend fun invoke(params: Trip): Result<Unit> = tripRepository.insertOrReplace(listOf(params))
}
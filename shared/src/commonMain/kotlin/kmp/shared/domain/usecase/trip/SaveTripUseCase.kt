package kmp.shared.domain.usecase.trip

import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.TripRepository
import kmp.shared.base.Result
import kmp.shared.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

interface SaveTripUseCase : UseCaseResult<Trip, Unit>

internal class SaveTripUseCaseImpl internal constructor(
    private val tripRepository: TripRepository,
    private val placeRepository: PlaceRepository
) : SaveTripUseCase {
    override suspend fun invoke(params: Trip): Result<Unit> {
        tripRepository.insertOrReplace( listOf(params) )
        params.itinerary.firstOrNull()?.let { placeRepository.insertOrReplace(it, params.name) }

        return Result.Success(Unit)
    }
}
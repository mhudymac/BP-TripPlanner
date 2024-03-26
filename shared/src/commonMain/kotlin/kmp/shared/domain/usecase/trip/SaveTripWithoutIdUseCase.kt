package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository

interface SaveTripWithoutIdUseCase : UseCaseResult<Trip, Unit>

internal class SaveTripWithoutIdUseCaseImpl internal constructor(
    private val tripRepository: TripRepository,
    private val placeRepository: PlaceRepository
) : SaveTripWithoutIdUseCase {
    override suspend fun invoke(params: Trip): Result<Unit> {
        val tripId = tripRepository.insertWithoutId( params )
        params.itinerary.let { placeRepository.insertOrReplace(it, tripId = tripId) }

        return Result.Success(Unit)
    }
}
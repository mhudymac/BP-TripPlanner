package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kmp.shared.domain.usecase.distances.GetDistancesUseCase

interface SaveTripWithoutIdUseCase : UseCaseResult<Trip, Unit>

internal class SaveTripWithoutIdUseCaseImpl internal constructor(
    private val tripRepository: TripRepository,
    private val placeRepository: PlaceRepository,
    private val getDistancesUseCase: GetDistancesUseCase
) : SaveTripWithoutIdUseCase {
    override suspend fun invoke(params: Trip): Result<Unit> {
        when (val tripWithDistances = getDistancesUseCase(params)) {
            is Result.Error -> return Result.Error(tripWithDistances.error)
            is Result.Success -> {
                val tripId = tripRepository.insertWithoutId( tripWithDistances.data )
                tripWithDistances.data.itinerary.let { placeRepository.insertOrReplace(it, tripId = tripId) }
                for((pair, distance) in tripWithDistances.data.distances){
                    placeRepository.saveDistance(pair.first, pair.second, distance)
                }
            }
        }
        return Result.Success(Unit)
    }
}
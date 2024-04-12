package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kmp.shared.domain.usecase.place.SaveDistancesUseCase

interface SaveTripUseCase : UseCaseResult<Pair<Trip,Boolean>, Unit>

internal class SaveTripUseCaseImpl internal constructor(
    private val tripRepository: TripRepository,
    private val placeRepository: PlaceRepository,
    private val saveDistancesUseCase: SaveDistancesUseCase
) : SaveTripUseCase {
    override suspend fun invoke(params: Pair<Trip,Boolean>): Result<Unit> {
        if(params.second){
            when (val tripWithDistances = saveDistancesUseCase(params.first)) {
                is Result.Error -> return Result.Error(tripWithDistances.error)
                is Result.Success -> {
                    tripRepository.insertOrReplace( listOf(params.first) )
                    placeRepository.insertOrReplace(params.first.itinerary, tripId = params.first.id)
                }
            }
        } else {
            return tripRepository.insertOrReplace( listOf(params.first) )
        }

        return Result.Success(Unit)
    }
}
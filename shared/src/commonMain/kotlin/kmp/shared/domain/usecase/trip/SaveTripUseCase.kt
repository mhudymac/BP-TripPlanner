package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kmp.shared.domain.usecase.distances.GetDistancesUseCase
import kmp.shared.system.Log

interface SaveTripUseCase : UseCaseResult<Pair<Trip,Boolean>, Unit>

internal class SaveTripUseCaseImpl internal constructor(
    private val tripRepository: TripRepository,
    private val placeRepository: PlaceRepository,
    private val getDistancesUseCase: GetDistancesUseCase
) : SaveTripUseCase {
    override suspend fun invoke(params: Pair<Trip,Boolean>): Result<Unit> {

        if(params.second){
            when (val tripWithDistances = getDistancesUseCase(params.first)) {
                is Result.Error -> return Result.Error(tripWithDistances.error)
                is Result.Success -> {
                    tripRepository.insertOrReplace( listOf(tripWithDistances.data) )

                    placeRepository.insertOrReplace(params.first.itinerary, tripId = tripWithDistances.data.id)

                    for((pair, distance) in tripWithDistances.data.distances){
                        placeRepository.saveDistance(pair.first, pair.second, distance)
                    }
                }
            }
        } else {
            tripRepository.insertOrReplace( listOf(params.first) )
            params.first.itinerary.let { placeRepository.insertOrReplace(it, tripId = params.first.id) }
        }

        return Result.Success(Unit)
    }
}
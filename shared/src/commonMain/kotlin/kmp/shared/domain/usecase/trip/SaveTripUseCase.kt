package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository

interface SaveTripUseCase : UseCaseResult<Pair<Trip,Boolean>, Unit>

internal class SaveTripUseCaseImpl internal constructor(
    private val tripRepository: TripRepository,
    private val placeRepository: PlaceRepository,
    private val updateDistancesUseCase: UpdateDistancesUseCase
) : SaveTripUseCase {

    /**
     * If the second parameter of the input pair is true, it updates all the places and distances of a trip.
     * If the second parameter of the input pair is false, it inserts or replaces the trip using the TripRepository.
     * If anything is an error, it returns an error
     *
     * @param params A pair of a Trip object and a Boolean value. The Trip object is the trip to save. The Boolean value indicates whether to update distances and places in the trip.
     * @return A Result object containing either Unit in case of success or an error.
     */
    override suspend fun invoke(params: Pair<Trip,Boolean>): Result<Unit> {
        if(params.second){
            when (val tripWithDistances = updateDistancesUseCase(params.first)) {
                is Result.Error -> return Result.Error(tripWithDistances.error)
                is Result.Success -> {
                    tripRepository.insertOrReplace( listOf(params.first) )
                    placeRepository.deleteByTripId(tripId = params.first.id)
                    placeRepository.insertOrReplace(places = params.first.itinerary, tripId = params.first.id)
                }
            }
        } else {
            return tripRepository.insertOrReplace( listOf(params.first) )
        }

        return Result.Success(Unit)
    }
}
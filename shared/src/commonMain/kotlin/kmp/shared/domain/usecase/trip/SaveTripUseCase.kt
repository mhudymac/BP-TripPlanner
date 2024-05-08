package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository

interface SaveTripUseCase : UseCaseResult<SaveTripUseCase.Params, Unit> {
    data class Params(val trip: Trip, val onlyUpdateTrip: Boolean)
}

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
    override suspend fun invoke(params: SaveTripUseCase.Params): Result<Unit> {
        if(!params.onlyUpdateTrip){
            when (val tripWithDistances = updateDistancesUseCase(params.trip)) {
                is Result.Error -> return Result.Error(tripWithDistances.error)
                is Result.Success -> {
                    tripRepository.insertOrReplace( listOf(params.trip) )
                    placeRepository.deleteByTripId(tripId = params.trip.id)
                    placeRepository.insertOrReplace(places = params.trip.itinerary, tripId = params.trip.id)
                }
            }
        } else {
            return tripRepository.insertOrReplace( listOf(params.trip) )
        }

        return Result.Success(Unit)
    }
}
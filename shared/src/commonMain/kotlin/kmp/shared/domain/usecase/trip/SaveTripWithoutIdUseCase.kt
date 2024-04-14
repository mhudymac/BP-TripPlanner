package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kmp.shared.domain.usecase.place.SaveDistancesUseCase

interface SaveTripWithoutIdUseCase : UseCaseResult<Pair<Trip,Boolean>, Unit>

internal class SaveTripWithoutIdUseCaseImpl internal constructor(
    private val tripRepository: TripRepository,
    private val placeRepository: PlaceRepository,
    private val saveDistancesUseCase: SaveDistancesUseCase,
    private val optimiseTripUseCase: OptimiseTripUseCase
) : SaveTripWithoutIdUseCase {
    override suspend fun invoke(params: Pair<Trip,Boolean>): Result<Unit> {

        /**
         * This function first inserts a trip without an id using the TripRepository.
         * If the trip is successfully inserted, it inserts or replaces the places in the trip using the PlaceRepository, saves the distances in the trip using the SaveDistancesUseCase, and optimises the trip using the OptimiseTripUseCase if the second parameter of the input pair is true.
         * If the trip is not successfully inserted, it returns an error.
         * If the places, distances, or trip are not successfully inserted, replaced, saved, or optimised, it returns an error.
         *
         * @param params A pair of a Trip object and a Boolean value. The Trip object is the trip to save. The Boolean value indicates whether to optimise the trip.
         * @return A Result object containing either Unit in case of success or an error.
         */
        return when(val tripId = tripRepository.insertWithoutId( params.first )){
            is Result.Success -> {
                params.first.itinerary.let { placeRepository.insertOrReplace(it, tripId = tripId.data) }

                val distances = saveDistancesUseCase(params.first.copy(id = tripId.data))
                if(distances is Result.Error)
                    return Result.Error(distances.error)

                if(params.second){
                    optimiseTripUseCase(params.first.copy(id = tripId.data))
                } else {
                    Result.Success(Unit)
                }
            }
            is Result.Error -> Result.Error(tripId.error)
        }
    }
}
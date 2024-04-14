package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.error.domain.TripError
import kmp.shared.base.usecase.UseCaseFlowResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.DistanceRepository
import kmp.shared.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GetTripUseCase: UseCaseFlowResult<Long, Trip>

internal class GetTripUseCaseImpl(
    private val tripRepository: TripRepository,
    private val distanceRepository: DistanceRepository
): GetTripUseCase {

    /**
     * This function first gets a trip by its id using the TripRepository.
     * If the trip is successfully retrieved, it gets the distances between places in the trip using the DistanceRepository and sets the distances of the trip.
     * If the trip is not successfully retrieved, it returns an error.
     * If the distances are not successfully retrieved, it returns an error.
     *
     * @param params The id of the trip to get.
     * @return A Flow of Result object containing either a Trip object in case of success or an error.
     */
    override suspend fun invoke(params: Long): Flow<Result<Trip>> {
        return tripRepository.getTripById(params).map { trip ->
            if (trip != null) {
                val distances = trip.order.windowed(2, 1, false).associate { (from, to) ->
                    when(val distance = distanceRepository.getDistance(from, to)){
                        is Result.Success -> Pair(from, to) to distance.data
                        is Result.Error -> return@map Result.Error(distance.error)
                    }
                }
                Result.Success(
                    trip.copy(
                        distances = distances
                    )
                )
            } else {
                Result.Error(error = TripError.GettingTripError)
            }
        }
    }
}


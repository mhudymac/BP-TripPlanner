package kmp.shared.domain.usecase.trip

import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseFlowResult
import kmp.shared.base.util.extension.getOrNull
import kmp.shared.base.util.extension.map
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.DistanceRepository
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GetTripUseCase: UseCaseFlowResult<Long, Trip>

internal class GetTripUseCaseImpl(
    private val tripRepository: TripRepository,
    private val distanceRepository: DistanceRepository
): GetTripUseCase {

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
                Result.Error(error = ErrorResult("Trip not found"))
            }
        }
    }
}


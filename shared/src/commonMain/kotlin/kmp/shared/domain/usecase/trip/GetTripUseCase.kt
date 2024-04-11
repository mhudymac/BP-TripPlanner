package kmp.shared.domain.usecase.trip

import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseFlowResult
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

                val distances = trip.order.indices.mapNotNull { originIndex ->
                    trip.order.indices.mapNotNull { destinationIndex ->
                        val pair = Pair(trip.order[originIndex], trip.order[destinationIndex])
                        distanceRepository.getDistance(pair.first, pair.second)?.let { distance ->
                            pair to distance
                        }?: return@map Result.Error(ErrorResult("Distance not found"))
                    }
                }.flatten().toMap()

                Result.Success(
                    trip.copy(
                        distances = distances
                    )
                )
            } else
                Result.Error(error = ErrorResult("Trip not found"))
        }
    }
}


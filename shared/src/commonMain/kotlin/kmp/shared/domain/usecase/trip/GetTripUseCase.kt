package kmp.shared.domain.usecase.trip

import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseFlowResult
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GetTripUseCase: UseCaseFlowResult<Long, Trip>

internal class GetTripUseCaseImpl(
    private val tripRepository: TripRepository,
    private val placeRepository: PlaceRepository,
): GetTripUseCase {



    override suspend fun invoke(params: Long): Flow<Result<Trip>> {

        return tripRepository.getTripById(params).map { trip ->
            if (trip != null) {
                val places = placeRepository.getPlacesByTripID(trip.id)
                val itinerary: List<Place> = trip.order.mapNotNull {
                    order -> places.firstOrNull { it.id == order } ?: return@map Result.Error(ErrorResult("Place not found"))
                }
                val distances = trip.order.indices.map { originIndex ->
                    trip.order.indices.map { destinationIndex ->
                        Pair(trip.order[originIndex], trip.order[destinationIndex]) to placeRepository.getDistance(trip.order[originIndex], trip.order[destinationIndex])
                    }
                }.flatten().mapNotNull { (pair, distance) ->
                    if(distance == null) return@map Result.Error(ErrorResult("Place not found"))
                    distance.let { pair to it }
                }.toMap()

                Result.Success(
                    trip.copy(
                        itinerary = itinerary,
                        distances = distances
                    )
                )
            } else
                Result.Error(error = ErrorResult("Trip not found"))
        }
    }
}


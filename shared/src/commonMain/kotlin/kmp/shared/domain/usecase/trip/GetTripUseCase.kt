package kmp.shared.domain.usecase.trip

import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseFlowResult
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
                Result.Success(
                    trip.copy(
                        itinerary = trip.order.map { order ->
                            places.firstOrNull { it.id == order } ?: throw IllegalStateException("Place not found")
                        }
                    )
                )
            } else
                Result.Error(error = ErrorResult("Trip not found"))
        }
    }

}


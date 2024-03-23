package kmp.shared.domain.usecase.trip

import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseFlowResultNoParams
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GetNearestTripUseCase: UseCaseFlowResultNoParams<Trip>
internal class GetNearestTripUseCaseImpl(
    private val tripRepository: TripRepository,
    private val placeRepository: PlaceRepository,
): GetNearestTripUseCase {

    override suspend fun invoke(): Flow<Result<Trip>> {
        return tripRepository.getNearestTrip().map { trip ->
            if (trip != null) {
                Result.Success(
                    trip.copy(
                        itinerary = trip.order.map { placeId ->
                            placeRepository.getPlacesById(placeId)
                        }.flatten()
                    )
                )
            } else
                Result.Error(error = ErrorResult("Trip not found"))
        }
    }

}

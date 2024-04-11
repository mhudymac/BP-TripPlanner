package kmp.shared.domain.usecase.trip

import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseFlowResultNoParams
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.DistanceRepository
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface GetNearestTripUseCase: UseCaseFlowResultNoParams<List<Trip>>
internal class GetNearestTripUseCaseImpl(
    private val tripRepository: TripRepository,
    private val getTripUseCase: GetTripUseCase
): GetNearestTripUseCase {

    override suspend fun invoke(): Flow<Result<List<Trip>>> {
        return tripRepository.getNearestTrip().map { trips ->
            Result.Success(
                trips.mapNotNull { oldTrip ->
                    when(val trip = getTripUseCase(oldTrip.id).first()){
                        is Result.Success -> trip.data
                        is Result.Error -> return@map Result.Error(trip.error)
                    }
                }
            )
        }
    }
}

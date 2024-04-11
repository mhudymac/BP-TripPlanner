package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.DistanceRepository
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface RepeatTripUseCase: UseCaseResult<Trip, Long>

internal class RepeatTripUseCaseImpl(
    private val tripRepository: TripRepository,
    private val placeRepository: PlaceRepository,
    private val distanceRepository: DistanceRepository,
    private val getTripUseCase: GetTripUseCase,
): RepeatTripUseCase {
    override suspend fun invoke(params: Trip): Result<Long> {
        when(val trip = getTripUseCase(params.id).first()){
            is Result.Success -> {
                val tripId = tripRepository.insertWithoutId(trip.data.copy(name = trip.data.name + " Repeated", completed = false, date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date))
                trip.data.itinerary.let { placeRepository.insertOrReplace(it, tripId = tripId) }
                for((pair, distance) in trip.data.distances){
                    distanceRepository.saveDistance(pair.first, pair.second, distance, tripId)
                }
                return Result.Success(tripId)
            }
            is Result.Error -> return Result.Error(trip.error)
        }
    }
}
package kmp.shared.domain.usecase.trip

import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.DistanceRepository
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kmp.shared.domain.usecase.place.SaveDistancesUseCase
import kmp.shared.system.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface RepeatTripUseCase: UseCaseResult<Trip, Long>

internal class RepeatTripUseCaseImpl(
    private val tripRepository: TripRepository,
    private val placeRepository: PlaceRepository,
    private val saveDistancesUseCase: SaveDistancesUseCase,
    private val getTripUseCase: GetTripUseCase,
): RepeatTripUseCase {
    override suspend fun invoke(params: Trip): Result<Long> {
        return when (val trip = getTripUseCase(params.id).first()) {
            is Result.Success -> {
                when(
                    tripRepository.insertOrReplace(
                        listOf(
                            trip.data.copy(
                                id = 0,
                                name = "",
                                completed = false,
                                date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                            )
                        )
                    )
                ){
                    is Result.Success -> {
                        trip.data.itinerary.let { placeRepository.insertOrReplace(it, tripId = 0L) }
                        saveDistancesUseCase(trip.data.copy(id = 0L))

                        Result.Success(0L)
                    }

                    is Result.Error -> Result.Error(ErrorResult("Error repeating trip"))
                }
            }
            is Result.Error -> Result.Error(trip.error)
        }
    }
}
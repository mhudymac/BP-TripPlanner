package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.error.domain.TripError
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kmp.shared.domain.usecase.place.SaveDistancesUseCase
import kotlinx.coroutines.flow.first
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

    /**
     * This function first gets a trip by its id using the GetTripUseCase.
     * If the trip is successfully retrieved, it inserts or replaces the trip using the TripRepository with a temporary id 0.
     * If the trip, places, or distances are not successfully inserted or replaced, it returns an error.
     *
     * @param params The trip to repeat.
     * @return A Result object containing either a tripId in case of success or an error.
     */
    override suspend fun invoke(params: Trip): Result<Long> {
        return when (val trip = getTripUseCase(GetTripUseCase.Params(params.id)).first()) {
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

                    is Result.Error -> Result.Error(TripError.RepeatingTripError)
                }
            }
            is Result.Error -> Result.Error(trip.error)
        }
    }
}
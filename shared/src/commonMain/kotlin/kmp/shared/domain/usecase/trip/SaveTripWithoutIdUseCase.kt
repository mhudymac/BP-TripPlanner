package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.DistanceRepository
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kmp.shared.domain.usecase.place.SaveDistancesUseCase
import kmp.shared.system.Log

interface SaveTripWithoutIdUseCase : UseCaseResult<Trip, Unit>

internal class SaveTripWithoutIdUseCaseImpl internal constructor(
    private val tripRepository: TripRepository,
    private val placeRepository: PlaceRepository,
    private val saveDistancesUseCase: SaveDistancesUseCase
) : SaveTripWithoutIdUseCase {
    override suspend fun invoke(params: Trip): Result<Unit> {
        when(val tripId = tripRepository.insertWithoutId( params )){
            is Result.Success -> {
                params.itinerary.let { placeRepository.insertOrReplace(it, tripId = tripId.data) }

                val distances = saveDistancesUseCase(params.copy(id = tripId.data))
                if(distances is Result.Error) return Result.Error(distances.error)
            }
            is Result.Error -> return Result.Error(tripId.error)
        }
        return Result.Success(Unit)
    }
}
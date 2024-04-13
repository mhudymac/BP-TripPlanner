package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.DistanceRepository
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kmp.shared.domain.usecase.place.SaveDistancesUseCase
import kmp.shared.system.Log

interface SaveTripWithoutIdUseCase : UseCaseResult<Pair<Trip,Boolean>, Unit>

internal class SaveTripWithoutIdUseCaseImpl internal constructor(
    private val tripRepository: TripRepository,
    private val placeRepository: PlaceRepository,
    private val saveDistancesUseCase: SaveDistancesUseCase,
    private val optimiseTripUseCase: OptimiseTripUseCase
) : SaveTripWithoutIdUseCase {
    override suspend fun invoke(params: Pair<Trip,Boolean>): Result<Unit> {
        return when(val tripId = tripRepository.insertWithoutId( params.first )){
            is Result.Success -> {
                params.first.itinerary.let { placeRepository.insertOrReplace(it, tripId = tripId.data) }

                val distances = saveDistancesUseCase(params.first.copy(id = tripId.data))
                if(distances is Result.Error)
                    return Result.Error(distances.error)

                if(params.second){
                    optimiseTripUseCase(params.first.copy(id = tripId.data))
                } else {
                    Result.Success(Unit)
                }
            }
            is Result.Error -> Result.Error(tripId.error)
        }
    }
}
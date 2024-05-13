package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.DistanceRepository
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.system.Log

interface UpdateDistancesUseCase: UseCaseResult<Trip, Unit>

internal class UpdateDistancesUseCaseImpl(
    private val placeRepository: PlaceRepository,
    private val distancesRepository: DistanceRepository
): UpdateDistancesUseCase {

    /**
     * This function first gets places by trip id using the PlaceRepository and calculates the new places that are not in the saved places.
     * It then updates the distance matrix using the PlaceRepository with the new places and the saved places.
     * If the distance matrix is successfully updated, it saves the distances in the trip using the DistanceRepository.
     * If the distance matrix is not successfully updated, it returns an error.
     *
     * @param params The trip to update distances for.
     * @return A Result object containing either Unit in case of success or an error.
     */
    override suspend fun invoke(params: Trip): Result<Unit> {
        val savedPlaces =  placeRepository.getPlacesByTripID(params.id).map { it.id }
        val newPlaces = params.order.minus(savedPlaces.toSet())
        if(newPlaces.isEmpty()) return Result.Success(Unit)

        when(val distances = placeRepository.updateDistanceMatrix(newPlaces, savedPlaces)){
            is Result.Success -> {
                distances.data.forEach {
                    val (origin, destination, distance) = it
                    distancesRepository.saveDistance(origin, destination, distance, params.id)
                }
            }
            is Result.Error -> return Result.Error(distances.error)
        }
        return Result.Success(Unit)
    }
}
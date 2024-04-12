package kmp.shared.domain.usecase.place

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.DistanceRepository
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.system.Log

interface SaveDistancesUseCase: UseCaseResult<Trip, Unit>

internal class SaveDistancesUseCaseImpl(
    private val placeRepository: PlaceRepository,
    private val distancesRepository: DistanceRepository
): SaveDistancesUseCase {
    override suspend fun invoke(params: Trip): Result<Unit> {
        when(val distances = placeRepository.getDistanceMatrix(params.order)){
            is Result.Success -> {
                params.order.indices.mapIndexed { i, originIndex ->
                    params.order.indices.mapIndexed { j, destinationIndex ->
                        Log.d("Saving distance-----------------", "${params.order[originIndex]} to ${params.order[destinationIndex]}: ${distances.data[i][j]}")
                        distancesRepository.saveDistance(params.order[originIndex], params.order[destinationIndex], distances.data[i][j], params.id)
                   }
                }
            }
            is Result.Error -> return Result.Error(distances.error)
        }
        return Result.Success(Unit)
    }
}
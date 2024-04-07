package kmp.shared.domain.usecase.distances

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.PlaceRepository

interface GetDistancesUseCase: UseCaseResult<Trip, Trip>

internal class GetDistancesUseCaseImpl(
    private val placeRepository: PlaceRepository,
): GetDistancesUseCase {
    override suspend fun invoke(params: Trip): Result<Trip> {
        return when(val distances = placeRepository.getDistanceMatrix(params.order)){
            is Result.Success -> {
                Result.Success(params.copy(
                    distances =
                        params.order.indices.mapIndexed { i, originIndex ->
                            params.order.indices.mapIndexed { j, destinationIndex ->
                                Pair(params.order[originIndex], params.order[destinationIndex]) to distances.data[i][j]
                           }
                        }.flatten().toMap()
                    )
                )
            }
            is Result.Error -> Result.Error(distances.error)
        }
    }
}
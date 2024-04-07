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
                val map = mutableMapOf<Pair<String, String>, Trip.Distance>()
                for (i in params.order.indices) {
                    for (j in params.order.indices) {
                        map[Pair(params.order[i], params.order[j])] = distances.data[i][j]
                    }
                }
                Result.Success(params.copy(distances = map.toMap()))
            }

            is Result.Error -> Result.Error(distances.error)
        }
    }
}
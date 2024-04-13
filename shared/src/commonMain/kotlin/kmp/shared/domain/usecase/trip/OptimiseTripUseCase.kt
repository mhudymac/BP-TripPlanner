package kmp.shared.domain.usecase.trip

import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.DistanceRepository
import kmp.shared.system.Log

interface OptimiseTripUseCase: UseCaseResult<Trip, Unit>

internal class OptimiseTripUseCaseImpl(
    private val distancesRepository: DistanceRepository,
    private val updateTripUseCase: UpdateOnlyTripDetailsUseCase,
): OptimiseTripUseCase {
    override suspend fun invoke(params: Trip): Result<Unit> {
        when (val distances = distancesRepository.getDistancesByTripId(params.id)) {
            is Result.Success -> {
                try {
                    val initialOrder = nearestNeighbor(distances.data, params.order)

                    val optimizedOrder = if(initialOrder.size > 4) threeOpt(distances.data, initialOrder) else initialOrder

                    return updateTripUseCase(params.copy(order = optimizedOrder))

                } catch (e: Exception) {
                    return Result.Error(ErrorResult(message = "Error optimising trip", throwable = e))
                }
            }
            is Result.Error -> return Result.Error(distances.error)
        }
    }

    private fun nearestNeighbor(distances: Map<Pair<String, String>, Trip.Distance>, places: List<String>): List<String> {
        val remainingPlaces = places.toMutableList()
        val path = mutableListOf<String>()

        var currentPlace = remainingPlaces.removeAt(0)
        path.add(currentPlace)

        while (remainingPlaces.isNotEmpty()) {
            val nextPlace = remainingPlaces.minByOrNull { place -> distances[Pair(currentPlace, place)]?.distance ?: Long.MAX_VALUE }
            if (nextPlace != null) {
                remainingPlaces.remove(nextPlace)
                path.add(nextPlace)
                currentPlace = nextPlace
            }
        }

        return path
    }

    private fun threeOpt(distances: Map<Pair<String, String>, Trip.Distance>, places: List<String>): List<String> {
        val path = places.toMutableList()
        var improved = true

        while (improved) {
            improved = false
            for (i in 0 until path.size - 2) {
                for (j in i + 1 until path.size - 1) {
                    for (k in j + 1 until path.size) {
                        val delta = changeInDistance(distances, path, i, j, k)
                        if (delta < 0) {
                            path.apply {
                                val tmp = this.subList(i + 1, j + 1).reversed() +
                                    this.subList(j + 1, k + 1).reversed() +
                                    this.subList(k + 1, this.size)
                                for (l in i + 1 until this.size) {
                                    this[l] = tmp[l - i - 1]
                                }
                            }
                            improved = true
                        }
                    }
                }
            }
        }

        return path
    }

    private fun changeInDistance(distances: Map<Pair<String, String>, Trip.Distance>, path: List<String>, i: Int, j: Int, k: Int): Long {
        val d0 = (distances[Pair(path[i], path[i + 1])]?.distance ?: Long.MAX_VALUE) +
            (distances[Pair(path[j], path[j + 1])]?.distance ?: Long.MAX_VALUE) +
            (distances[Pair(path[k], path[(k + 1) % path.size])]?.distance ?: Long.MAX_VALUE)
        val d1 = (distances[Pair(path[i], path[j])]?.distance ?: Long.MAX_VALUE) +
            (distances[Pair(path[i + 1], path[k])]?.distance ?: Long.MAX_VALUE) +
            (distances[Pair(path[j + 1], path[(k + 1) % path.size])]?.distance ?: Long.MAX_VALUE)
        val d2 = (distances[Pair(path[i], path[k])]?.distance ?: Long.MAX_VALUE) +
            (distances[Pair(path[i + 1], path[j + 1])]?.distance ?: Long.MAX_VALUE) +
            (distances[Pair(path[j], path[(k + 1) % path.size])]?.distance ?: Long.MAX_VALUE)
        val d3 = (distances[Pair(path[i], path[j + 1])]?.distance ?: Long.MAX_VALUE) +
            (distances[Pair(path[k], path[i + 1])]?.distance ?: Long.MAX_VALUE) +
            (distances[Pair(path[j], path[(k + 1) % path.size])]?.distance ?: Long.MAX_VALUE)

        return minOf(d1, d2, d3) - d0
    }
}
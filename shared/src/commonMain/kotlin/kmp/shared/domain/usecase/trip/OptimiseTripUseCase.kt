package kmp.shared.domain.usecase.trip

import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.base.error.domain.TripError
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Distance
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.DistanceRepository
import kmp.shared.system.Log

interface OptimiseTripUseCase: UseCaseResult<Trip, Unit>

internal class OptimiseTripUseCaseImpl(
    private val distancesRepository: DistanceRepository,
    private val updateTripUseCase: UpdateOnlyTripDetailsUseCase,
): OptimiseTripUseCase {

    /**
     * This function first gets distances between places in the trip using the DistanceRepository.
     * If the distances are successfully retrieved, it optimises the order of places in the trip using the nearest neighbor and 3-opt algorithms and updates the trip details using the UpdateOnlyTripDetailsUseCase.
     * If the distances are not successfully retrieved, it returns an error.
     * If the trip details are not successfully updated, it returns an error.
     *
     * @param params The trip to optimise.
     * @return A Result object containing either Unit in case of success or an error.
     */
    override suspend fun invoke(params: Trip): Result<Unit> {
        when (val distances = distancesRepository.getDistancesByTripId(params.id)) {
            is Result.Success -> {
                try {
                    val initialOrder = nearestNeighbor(distances.data, params.order)

                    val optimizedOrder = if(initialOrder.size > 4) threeOpt(distances.data, initialOrder) else initialOrder

                    return updateTripUseCase(params.copy(order = optimizedOrder))

                } catch (e: Exception) {
                    return Result.Error(TripError.OptimisingTripError)
                }
            }
            is Result.Error -> return Result.Error(distances.error)
        }
    }

    /**
     * This function is an implementation of nearest neighbour algorithm.
     * It starts from the first place and finds the nearest neighbor for each subsequent place.
     *
     * @param distances The distances between places in the trip.
     * @param places The places in the trip.
     * @return The order of places in the trip after applying the nearest neighbor algorithm.
     */
    private fun nearestNeighbor(distances: Map<Pair<String, String>, Distance>, places: List<String>): List<String> {
        val remainingPlaces = places.toMutableList()
        val path = mutableListOf<String>()

        var currentPlace = remainingPlaces.removeAt(0)
        path.add(currentPlace)

        while (remainingPlaces.isNotEmpty()) {
            val nextPlace = remainingPlaces.minBy { place -> Log.d(currentPlace, place); distances[Pair(currentPlace, place)]?.distance ?: Long.MAX_VALUE}
            remainingPlaces.remove(nextPlace)
            path.add(nextPlace)
            currentPlace = nextPlace
        }

        return path
    }

    /**
     * This function is used to optimise the order of places in the trip using the 3-opt algorithm.
     * It iteratively improves the order of places by swapping three places at a time until no more improvements can be made.
     *
     * @param distances The distances between places in the trip.
     * @param places The places in the trip.
     * @return The order of places in the trip after applying the 3-opt algorithm.
     */
    private fun threeOpt(distances: Map<Pair<String, String>, Distance>, places: List<String>): List<String> {
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

    /**
     * This function is used to calculate the change in distance when swapping three places in the trip.
     *
     * @param distances The distances between places in the trip.
     * @param path The current order of places in the trip.
     * @param i The index of the first place to swap.
     * @param j The index of the second place to swap.
     * @param k The index of the third place to swap.
     * @return The change in distance when swapping the three places.
     */
    private fun changeInDistance(distances: Map<Pair<String, String>, Distance>, path: List<String>, i: Int, j: Int, k: Int): Long {
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
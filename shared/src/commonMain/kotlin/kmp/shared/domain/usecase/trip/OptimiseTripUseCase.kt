package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.error.domain.TripError
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Distance
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.DistanceRepository

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
        return when (val distances = distancesRepository.getDistancesByTripId(params.id)) {
            is Result.Success -> {
                try {
                    val initialOrder = nearestNeighbor(distances.data, params.order)

                    val optimizedOrder =  twoOpt(distances.data, initialOrder)

                    updateTripUseCase(params.copy(order = optimizedOrder))

                } catch (e: Exception) {
                    Result.Error(TripError.OptimisingTripError)
                }
            }

            is Result.Error -> Result.Error(distances.error)
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
            val nextPlace = remainingPlaces.minBy { place -> distances[Pair(currentPlace, place)]?.distance ?: Long.MAX_VALUE}
            remainingPlaces.remove(nextPlace)
            path.add(nextPlace)
            currentPlace = nextPlace
        }

        return path
    }

    /**
     * This function is an implementation of the 2-opt algorithm.
     * It iteratively improves the order of places in the trip by swapping two places if it results in a shorter total distance.
     *
     * @param distances The distances between places in the trip.
     * @param initialOrder The initial order of places in the trip.
     * @return The order of places in the trip after applying the 2-opt algorithm.
     */
    private fun twoOpt(distances: Map<Pair<String, String>, Distance>, initialOrder: List<String>): List<String> {
        var bestOrder = initialOrder
        var bestDistance = calculateTotalDistance(distances, bestOrder)
        var improvement = true

        while (improvement) {
            improvement = false
            for (i in 0 until bestOrder.size - 1) {
                for (j in i + 1 until bestOrder.size) {
                    val newOrder = bestOrder.toMutableList()
                    newOrder.subList(i + 1, j + 1).reverse()
                    val newDistance = calculateTotalDistance(distances, newOrder)
                    if (newDistance < bestDistance) {
                        bestOrder = newOrder
                        bestDistance = newDistance
                        improvement = true
                    }
                }
            }
        }

        return bestOrder
    }

    /**
     * This function calculates the total distance of a trip given an order of places.
     *
     * @param distances The distances between places in the trip.
     * @param order The order of places in the trip.
     * @return The total distance of the trip.
     */
    private fun calculateTotalDistance(distances: Map<Pair<String, String>, Distance>, order: List<String>): Long {
        var totalDistance = 0L
        for (i in 0 until order.size - 1) {
            val from = order[i]
            val to = order[i + 1]
            totalDistance += distances[Pair(from, to)]?.distance ?: Long.MAX_VALUE
        }
        return totalDistance
    }
}
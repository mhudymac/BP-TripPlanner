package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.DistanceRepository
import kmp.shared.domain.repository.PhotoRepository
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository

interface DeleteTripUseCase: UseCaseResult<Trip, Unit>

internal class DeleteTripUseCaseImpl internal constructor(
    private val tripRepository: TripRepository,
    private val placeRepository: PlaceRepository,
    private val distanceRepository: DistanceRepository,
    private val photoRepository: PhotoRepository
): DeleteTripUseCase {

    /**
     * It first deletes the trip using the TripRepository.
     * If the trip is successfully deleted, it deletes the places, distances, and photos associated with the trip using the PlaceRepository, DistanceRepository, and PhotoRepository respectively.
     * If the trip, places, distances, or photos are not successfully deleted, it returns the error.
     *
     * @param params The trip to delete.
     * @return A Result object containing either Unit in case of success or an error.
     */
    override suspend fun invoke(params: Trip): Result<Unit> {
        val trip = tripRepository.deleteTripById(params.id)
        if(trip is Result.Error) return trip

        val place = placeRepository.deleteByTripId(params.id)
        if(place is Result.Error) return place

        val distance = distanceRepository.deleteDistancesByTripId(params.id)
        if(distance is Result.Error) return distance

        val photo = photoRepository.deletePhotoByTripId(params.id)
        if(photo is Result.Error) return photo

        return Result.Success(Unit)
    }
}
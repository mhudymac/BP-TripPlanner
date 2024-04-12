package kmp.shared.domain.usecase.trip

import kmp.shared.base.ErrorResult
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
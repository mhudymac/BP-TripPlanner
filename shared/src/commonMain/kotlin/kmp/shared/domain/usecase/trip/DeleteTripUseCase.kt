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
    override suspend fun invoke(params: Trip): Result<Unit> {
        tripRepository.deleteTripById(params.id)
        placeRepository.deleteByTripId(params.id)
        distanceRepository.deleteDistancesByTripId(params.id)
        photoRepository.deletePhotoByTripId(params.id)

        return Result.Success(Unit)
    }
}
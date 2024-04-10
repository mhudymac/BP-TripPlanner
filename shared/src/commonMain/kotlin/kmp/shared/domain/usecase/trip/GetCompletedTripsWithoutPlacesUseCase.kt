package kmp.shared.domain.usecase.trip

import kmp.shared.base.usecase.UseCaseFlowNoParams
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow

interface GetCompletedTripsWithoutPlacesUseCase : UseCaseFlowNoParams<List<Trip>>

internal class GetCompletedTripsWithoutPlacesUseCaseImpl(
    private val tripRepository: TripRepository
) : GetCompletedTripsWithoutPlacesUseCase {
    override suspend fun invoke(): Flow<List<Trip>> {
        return tripRepository.getCompletedTrips()
    }
}
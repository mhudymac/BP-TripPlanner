package kmp.shared.domain.usecase.trip

import kmp.shared.base.usecase.UseCaseFlowNoParams
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow

interface GetUncompletedTripsWithoutPlacesUseCase : UseCaseFlowNoParams<List<Trip>>

internal class GetUncompletedTripsWithoutPlacesUseCaseImpl(
    private val tripRepository: TripRepository
) : GetUncompletedTripsWithoutPlacesUseCase {
    override suspend fun invoke(): Flow<List<Trip>> {
        return tripRepository.getUncompletedTrips()
    }
}
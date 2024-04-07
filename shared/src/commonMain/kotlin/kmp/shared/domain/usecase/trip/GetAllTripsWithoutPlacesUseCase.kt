package kmp.shared.domain.usecase.trip

import kmp.shared.base.usecase.UseCaseFlowNoParams
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GetAllTripsWithoutPlacesUseCase : UseCaseFlowNoParams<List<Trip>>

internal class GetAllTripsWithoutPlacesUseCaseImpl(
    private val tripRepository: TripRepository
) : GetAllTripsWithoutPlacesUseCase {
    override suspend fun invoke(): Flow<List<Trip>> {
        return tripRepository.getAllTrips()
    }
}
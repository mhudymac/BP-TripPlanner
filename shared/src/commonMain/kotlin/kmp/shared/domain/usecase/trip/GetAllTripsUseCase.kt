package kmp.shared.domain.usecase.trip

import kmp.shared.base.usecase.UseCaseFlowNoParams
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kotlinx.coroutines.flow.map

interface GetAllTripsUseCase : UseCaseFlowNoParams<List<Trip>>

internal class GetAllTripsUseCaseImpl(
    private val tripRepository: TripRepository,
    private val placeRepository: PlaceRepository
) : GetAllTripsUseCase {
    override suspend fun invoke() = tripRepository.getAllTrips().map { trips ->
        trips.map { trip ->
            val places = placeRepository.getPlacesByTrip(trip.name)
            trip.copy(
                itinerary = places
            )
        }
    }
}
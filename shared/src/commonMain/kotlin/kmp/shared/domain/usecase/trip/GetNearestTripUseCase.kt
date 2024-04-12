package kmp.shared.domain.usecase.trip

import kmp.shared.base.usecase.UseCaseFlowNoParams
import kmp.shared.base.util.extension.getOrNull
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.TripRepository
import kmp.shared.domain.usecase.location.GetLocationFlowUseCase
import kmp.shared.domain.usecase.photos.GetPhotosByTripUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

interface GetNearestTripUseCase: UseCaseFlowNoParams<List<Trip>>
internal class GetNearestTripUseCaseImpl(
    private val tripRepository: TripRepository,
    private val getTripUseCase: GetTripUseCase,
    private val getLocationFlowUseCase: GetLocationFlowUseCase,
    private val getPhotosByTripUseCase: GetPhotosByTripUseCase
): GetNearestTripUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun invoke(): Flow<List<Trip>> {
        return getLocationFlowUseCase().flatMapLatest { location ->
            tripRepository.getNearestTrip().flatMapLatest inner@ { trips ->
                if (trips.isEmpty()) {
                    return@inner flowOf(emptyList())
                }

                trips.mapNotNull { trip ->
                    getTripUseCase(trip.id).first().getOrNull()
                }.let { trips1 ->
                    combine(trips1.map { trip ->
                        getPhotosByTripUseCase(trip.id).map { photos ->
                            trip.photos = photos
                            trip
                        }
                    }) { it.toList() }
                }.map { trips2 ->
                    trips2.onEach { trip ->
                        val closestPlace = trip.itinerary.minByOrNull { place ->
                            distanceBetween(location, place.location)
                        }
                        if (closestPlace != null && distanceBetween(location, closestPlace.location) < 0.2) {
                            trip.activePlace = closestPlace.id
                        }
                    }
                }
            }
        }
    }

    private fun distanceBetween(location1: Location, location2: Location): Double {
        val lat1 = location1.latitude.toRadians()
        val lon1 = location1.longitude.toRadians()
        val lat2 = location2.latitude.toRadians()
        val lon2 = location2.longitude.toRadians()

        val a = sin((lat2 - lat1) / 2).pow(2.0) +
            cos(lat1) * cos(lat2) * sin((lon2 - lon1) / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS * c
    }

    companion object {
        const val EARTH_RADIUS = 6371.0
    }

    private fun Double.toRadians(): Double = this * (PI / 180)
}

//override suspend fun invoke(): Flow<List<Trip>> {
//    return getLocationFlowUseCase().flatMapLatest { location ->
//        tripRepository.getNearestTrip().map { trips ->
//            trips.mapNotNull { trip ->
//                getTripUseCase(trip.id).first().getOrNull()
//            }.onEach { trip ->
//                val closestPlace = trip.itinerary.minByOrNull { place ->
//                    distanceBetween(location, place.location)
//                }
//                if (closestPlace != null && distanceBetween(location, closestPlace.location) < 0.2) {
//                    trip.activePlace = closestPlace.id
//                }
//
//            }
//        }
//    }
//}
package kmp.shared.domain.usecase.trip

import kmp.shared.base.Result
import kmp.shared.base.error.domain.TripError
import kmp.shared.base.usecase.UseCaseFlowNoParams
import kmp.shared.base.util.extension.getOrNull
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.TripRepository
import kmp.shared.domain.usecase.location.GetLocationFlowUseCase
import kmp.shared.domain.usecase.photos.GetPhotosByTripUseCase
import kmp.shared.system.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

const val ACTIVE_DISTANCE = 0.2

interface GetNearestTripUseCase: UseCaseFlowNoParams<List<Trip>>
internal class GetNearestTripUseCaseImpl(
    private val tripRepository: TripRepository,
    private val getTripUseCase: GetTripUseCase,
    private val getLocationFlowUseCase: GetLocationFlowUseCase,
    private val getPhotosByTripUseCase: GetPhotosByTripUseCase
): GetNearestTripUseCase {

    /**
     * This function first gets the current location using the GetLocationFlowUseCase.
     * Then, it gets the nearest trip using the TripRepository.
     * If there are no trips, it returns a flow of empty list.
     * If there are trips, it gets each trip by its id using the GetTripUseCase and sets the photos of each trip using the GetPhotosByTripUseCase.
     * Finally, it sets the active place of each trip to the closest place to the current location if the distance is less than 0.2.
     *
     * @return A Flow of list of Trip objects.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun invoke(): Flow<List<Trip>> {
        return getLocationFlowUseCase().flatMapLatest { location ->
            tripRepository.getNearestTrip().flatMapLatest inner@ { trips ->
                if (trips.isEmpty()) {
                    return@inner flowOf(emptyList())
                }

                trips.mapNotNull { trip ->
                    getTripUseCase(GetTripUseCase.Params(trip.id)).first().getOrNull()
                }.let { trips1 ->
                    combine(trips1.map { trip ->
                        getPhotosByTripUseCase(GetPhotosByTripUseCase.Params(trip.id)).map { photos ->
                            trip.copy(photos = photos)
                        }
                    }) { it.toList() }
                }.map { trips2 ->
                    trips2.map { trip ->
                        if(location is Result.Success ) {
                            val closestPlace = trip.itinerary.minByOrNull { place ->
                                distanceBetween(location.data, place.location)
                            }
                            if (closestPlace != null && distanceBetween(
                                    location.data,
                                    closestPlace.location
                                ) < ACTIVE_DISTANCE
                            ) {
                                trip.copy(activePlace = closestPlace.id)
                            } else {
                                trip
                            }
                        } else {
                            trip
                        }
                    }
                }
            }
        }
    }

    /**
     * This function is used to calculate the distance between two locations.
     * It uses the haversine formula to calculate the great-circle distance between two points on a sphere given their longitudes and latitudes.
     *
     * @param location1 The first location.
     * @param location2 The second location.
     * @return The distance between the two locations.
     */
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

    private fun Double.toRadians(): Double = this * (PI / 180)

    companion object {
        const val EARTH_RADIUS = 6371.0
    }
}
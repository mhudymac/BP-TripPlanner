package kmp.shared.usecase.trip

import kmp.shared.base.Result
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Photo
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.MockTripRepository
import kmp.shared.domain.repository.TripRepository
import kmp.shared.domain.usecase.location.GetLocationFlowUseCase
import kmp.shared.domain.usecase.location.MockGetLocationFlowUseCase
import kmp.shared.domain.usecase.photos.GetPhotosByTripUseCase
import kmp.shared.domain.usecase.photos.MockGetPhotosByTripUseCase
import kmp.shared.domain.usecase.trip.GetNearestTripUseCase
import kmp.shared.domain.usecase.trip.GetNearestTripUseCaseImpl
import kmp.shared.domain.usecase.trip.GetTripUseCase
import kmp.shared.domain.usecase.trip.MockGetTripUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import org.kodein.mock.Mocker
import org.kodein.mock.UsesMocks
import kotlin.test.Test
import kotlin.test.assertEquals

@UsesMocks(TripRepository::class, GetTripUseCase::class, GetLocationFlowUseCase::class, GetPhotosByTripUseCase::class)
class GetNearestTripUseCaseTest {

    private val mocker = Mocker()
    private val mockTripRepository: TripRepository = MockTripRepository(mocker)
    private val mockGetTripUseCase: GetTripUseCase = MockGetTripUseCase(mocker)
    private val mockGetLocationFlowUseCase: GetLocationFlowUseCase = MockGetLocationFlowUseCase(mocker)
    private val mockGetPhotosByTripUseCase: GetPhotosByTripUseCase = MockGetPhotosByTripUseCase(mocker)

    private val getNearestTripUseCase: GetNearestTripUseCase = GetNearestTripUseCaseImpl(
        mockTripRepository,
        mockGetTripUseCase,
        mockGetLocationFlowUseCase,
        mockGetPhotosByTripUseCase
    )

    @Test
    fun `invoke returns empty list when there are no trips`() = runBlocking {
        // Setup
        val location = Result.Success(Location(0.0, 0.0))
        mocker.everySuspending { mockGetLocationFlowUseCase() } returns flowOf(location)
        mocker.everySuspending { mockTripRepository.getNearestTrip() } returns flowOf(emptyList())

        // Execute
        val result = mutableListOf<Trip>()
        getNearestTripUseCase().collect { result.addAll(it) }

        // Verify
        assertEquals(emptyList(), result)
        mocker.verifyWithSuspend {
            mockGetLocationFlowUseCase()
            mockTripRepository.getNearestTrip()
        }
    }

    @Test
    fun `invoke returns trips with no photos when there are trips but no photos`() = runBlocking {
        // Setup
        val location = Result.Success(Location(0.0, 0.0))
        val trip = Trip(id = 1L, name = "Test Trip", itinerary = listOf(), date = LocalDate(year = 2024, month = Month.JANUARY, dayOfMonth = 5))
        mocker.everySuspending { mockGetLocationFlowUseCase() } returns flowOf(location)
        mocker.everySuspending { mockTripRepository.getNearestTrip() } returns flowOf(listOf(trip))
        mocker.everySuspending { mockGetTripUseCase(GetTripUseCase.Params(trip.id)) } returns flowOf(Result.Success(trip))
        mocker.everySuspending { mockGetPhotosByTripUseCase(GetPhotosByTripUseCase.Params(trip.id)) } returns flowOf(emptyList())

        // Execute
        val result = mutableListOf<Trip>()
        getNearestTripUseCase().collect { result.addAll(it) }

        // Verify
        assertEquals(listOf(trip), result)
        mocker.verifyWithSuspend {
            mockGetLocationFlowUseCase()
            mockTripRepository.getNearestTrip()
            mockGetTripUseCase(GetTripUseCase.Params(trip.id))
            mockGetPhotosByTripUseCase(GetPhotosByTripUseCase.Params(trip.id))
        }
    }

    @Test
    fun `invoke returns trips with photos when there are trips and photos`() = runBlocking {
        // Setup
        val location = Result.Success(Location(0.0, 0.0))
        val trip = Trip(id = 1L, name = "Test Trip", itinerary = listOf(), date = LocalDate(year = 2024, month = Month.JANUARY, dayOfMonth = 5))
        val photo = Photo(placeId = "place", tripId = trip.id, photoUri = "http://example.com/photo.jpg")
        mocker.everySuspending { mockGetLocationFlowUseCase() } returns flowOf(location)
        mocker.everySuspending { mockTripRepository.getNearestTrip() } returns flowOf(listOf(trip))
        mocker.everySuspending { mockGetTripUseCase(GetTripUseCase.Params(trip.id)) } returns flowOf(Result.Success(trip))
        mocker.everySuspending { mockGetPhotosByTripUseCase(GetPhotosByTripUseCase.Params(trip.id)) } returns flowOf(listOf(photo))

        // Execute
        val result = mutableListOf<Trip>()
        getNearestTripUseCase().collect { result.addAll(it) }

        // Verify
        assertEquals(listOf(trip.copy(photos = listOf(photo))), result)
        mocker.verifyWithSuspend {
            mockGetLocationFlowUseCase()
            mockTripRepository.getNearestTrip()
            mockGetTripUseCase(GetTripUseCase.Params(trip.id))
            mockGetPhotosByTripUseCase(GetPhotosByTripUseCase.Params(trip.id))
        }
    }

    @Test
    fun `invoke returns multiple trips when there are multiple trips`() = runBlocking {
        // Setup
        val location = Result.Success(Location(0.0, 0.0))
        val trip1 = Trip(id = 1L, name = "Test Trip 1", itinerary = listOf(), date = LocalDate(year = 2024, month = Month.JANUARY, dayOfMonth = 5))
        val trip2 = Trip(id = 2L, name = "Test Trip 2", itinerary = listOf(), date = LocalDate(year = 2024, month = Month.JANUARY, dayOfMonth = 5))
        mocker.everySuspending { mockGetLocationFlowUseCase() } returns flowOf(location)
        mocker.everySuspending { mockTripRepository.getNearestTrip() } returns flowOf(listOf(trip1, trip2))
        mocker.everySuspending { mockGetTripUseCase(GetTripUseCase.Params(trip1.id)) } returns flowOf(Result.Success(trip1))
        mocker.everySuspending { mockGetTripUseCase(GetTripUseCase.Params(trip2.id)) } returns flowOf(Result.Success(trip2))
        mocker.everySuspending { mockGetPhotosByTripUseCase(GetPhotosByTripUseCase.Params(trip1.id)) } returns flowOf(emptyList())
        mocker.everySuspending { mockGetPhotosByTripUseCase(GetPhotosByTripUseCase.Params(trip2.id)) } returns flowOf(emptyList())

        // Execute
        val result = mutableListOf<Trip>()
        getNearestTripUseCase().collect { result.addAll(it) }

        // Verify
        assertEquals(listOf(trip1, trip2), result)
        mocker.verifyWithSuspend {
            mockGetLocationFlowUseCase()
            mockTripRepository.getNearestTrip()
            mockGetTripUseCase(GetTripUseCase.Params(trip1.id))
            mockGetTripUseCase(GetTripUseCase.Params(trip2.id))
            mockGetPhotosByTripUseCase(GetPhotosByTripUseCase.Params(trip1.id))
            mockGetPhotosByTripUseCase(GetPhotosByTripUseCase.Params(trip2.id))
        }
    }

    @Test
    fun `invokes returns trip with an active place if it's location matches`() = runBlocking {
        // Setup
        val location = Result.Success(Location(0.0, 0.0))
        val place = Place(id = "place", name = "Place", formattedAddress = "", location = Location(0.0, 0.0), googleMapsUri = "", photoId = "")
        val trip = Trip(id = 1L, name = "Test Trip", itinerary = listOf(place), date = LocalDate(year = 2024, month = Month.JANUARY, dayOfMonth = 5))
        val photo = Photo(placeId = "place", tripId = trip.id, photoUri = "http://example.com/photo.jpg")
        mocker.everySuspending { mockGetLocationFlowUseCase() } returns flowOf(location)
        mocker.everySuspending { mockTripRepository.getNearestTrip() } returns flowOf(listOf(trip))
        mocker.everySuspending { mockGetTripUseCase(GetTripUseCase.Params(trip.id)) } returns flowOf(Result.Success(trip))
        mocker.everySuspending { mockGetPhotosByTripUseCase(GetPhotosByTripUseCase.Params(trip.id)) } returns flowOf(listOf(photo))

        // Execute
        val result = mutableListOf<Trip>()
        getNearestTripUseCase().collect { result.addAll(it) }

        // Verify
        assertEquals(place.id, result.first().activePlace)
        mocker.verifyWithSuspend {
            mockGetLocationFlowUseCase()
            mockTripRepository.getNearestTrip()
            mockGetTripUseCase(GetTripUseCase.Params(trip.id))
            mockGetPhotosByTripUseCase(GetPhotosByTripUseCase.Params(trip.id))
        }
    }
}
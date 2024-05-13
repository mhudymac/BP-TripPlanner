package kmp.shared.usecase.place

import kmp.shared.base.Result
import kmp.shared.base.error.domain.TripError
import kmp.shared.domain.model.Distance
import kmp.shared.domain.model.Trip
import kmp.shared.domain.repository.DistanceRepository
import kmp.shared.domain.repository.MockDistanceRepository
import kmp.shared.domain.repository.MockPlaceRepository
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.usecase.place.SaveDistancesUseCase
import kmp.shared.domain.usecase.place.SaveDistancesUseCaseImpl
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import org.kodein.mock.Mocker
import org.kodein.mock.UsesMocks
import kotlin.test.Test
import kotlin.test.assertEquals

@UsesMocks(PlaceRepository::class, DistanceRepository::class)
class SaveDistancesUseCaseTest {

    private val mocker = Mocker()
    private val mockPlaceRepository: PlaceRepository = MockPlaceRepository(mocker)
    private val mockDistanceRepository: DistanceRepository = MockDistanceRepository(mocker)
    private val saveDistancesUseCase: SaveDistancesUseCase = SaveDistancesUseCaseImpl(mockPlaceRepository, mockDistanceRepository)

    private val trip = Trip(id = 1L, name = "Test Trip", itinerary = listOf(), date = LocalDate(year = 2024, month = Month.JANUARY, dayOfMonth = 5)) // Initialize with appropriate values
    private val distance = Distance(10, 10)
    private val results = listOf(Triple("origin", "destination", distance), Triple("origin2", "destination2", distance))

    @Test
    fun `invoke returns Success when distances are successfully retrieved and saved`() = runBlocking {
        // Setup
        mocker.everySuspending { mockPlaceRepository.getDistanceMatrix(trip.order) } returns Result.Success(results)
        mocker.everySuspending { mockDistanceRepository.saveDistance("origin", "destination", distance, trip.id) } returns Result.Success(Unit)
        mocker.everySuspending { mockDistanceRepository.saveDistance("origin2", "destination2", distance, trip.id) } returns Result.Success(Unit)

        // Execute
        val result = saveDistancesUseCase(trip)

        // Verify
        mocker.verifyWithSuspend {
            mockPlaceRepository.getDistanceMatrix(trip.order)
            results.forEach { (origin, destination, distance) ->
                mockDistanceRepository.saveDistance(origin, destination, distance, trip.id)
            }
        }

        assertEquals(Result.Success(Unit), result)
    }

    @Test
    fun `invoke return saving distance error when it occurs`() = runBlocking {
        // Setup
        mocker.everySuspending { mockPlaceRepository.getDistanceMatrix(trip.order) } returns Result.Success(results)
        mocker.everySuspending { mockDistanceRepository.saveDistance("origin", "destination", distance, trip.id) } returns Result.Success(Unit)
        mocker.everySuspending { mockDistanceRepository.saveDistance("origin2", "destination2", distance, trip.id) } returns Result.Error(TripError.SavingDistanceError)

        // Execute
        val result = saveDistancesUseCase.invoke(trip)

        // Verify
        mocker.verifyWithSuspend {
            mockPlaceRepository.getDistanceMatrix(trip.order)
            results.forEach { (origin, destination, distance) ->
                mockDistanceRepository.saveDistance(origin, destination, distance, trip.id)
            }
        }

        assertEquals(Result.Error(TripError.SavingDistanceError), result)
    }

    @Test
    fun `invoke returns getting distances error when distances retrieval fails`() = runBlocking {
        // Setup
        mocker.everySuspending { mockPlaceRepository.getDistanceMatrix(trip.order) } returns Result.Error(TripError.GettingDistancesError)

        // Execute
        val result = saveDistancesUseCase.invoke(trip)

        // Verify
        assertEquals(Result.Error(TripError.GettingDistancesError), result)
    }
}
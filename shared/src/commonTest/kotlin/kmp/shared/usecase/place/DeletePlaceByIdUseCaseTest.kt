package kmp.shared.usecase.place

import kmp.shared.base.Result
import kmp.shared.base.error.domain.TripError
import kmp.shared.domain.repository.MockPlaceRepository
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.usecase.place.DeletePlaceByIdUseCase
import kmp.shared.domain.usecase.place.DeletePlaceByIdUseCaseImpl
import kotlinx.coroutines.runBlocking
import org.kodein.mock.Mocker
import org.kodein.mock.UsesMocks
import kotlin.test.Test
import kotlin.test.assertEquals

@UsesMocks(PlaceRepository::class)
class DeletePlaceByIdUseCaseTest {

    private val mocker = Mocker()
    private val mockRepository: PlaceRepository = MockPlaceRepository(mocker)
    private val deletePlaceByIdUseCase: DeletePlaceByIdUseCase = DeletePlaceByIdUseCaseImpl(mockRepository)

    private val params = DeletePlaceByIdUseCase.Params("id", 1L)

    @Test
    fun `should return success if repository returns success`() = runBlocking {
        // Setup
        mocker.everySuspending { mockRepository.deleteById(params.placeId, params.tripId) } returns Result.Success(Unit)

        // Execute
        val result = deletePlaceByIdUseCase(params)

        // Verify
        assertEquals(Result.Success(Unit), result)
    }

    @Test
    fun `should return error if repository returns error`() = runBlocking {
        // Setup
        mocker.everySuspending { mockRepository.deleteById(params.placeId, params.tripId) } returns Result.Error(TripError.DeletingPlaceError)

        // Execute
        val result = deletePlaceByIdUseCase(params)

        // Verify
        assertEquals(Result.Error(TripError.DeletingPlaceError), result)
    }
}
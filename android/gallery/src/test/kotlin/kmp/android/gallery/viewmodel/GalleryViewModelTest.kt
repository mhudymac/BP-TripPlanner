package kmp.android.gallery.viewmodel

import kmp.shared.base.Result
import kmp.shared.base.error.domain.TripError
import kmp.shared.domain.model.Photo
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.photos.GetPhotosByTripUseCase
import kmp.shared.domain.usecase.photos.RemovePhotoByUriUseCase
import kmp.shared.domain.usecase.photos.SavePhotoUseCase
import kmp.shared.domain.usecase.trip.DeleteTripUseCase
import kmp.shared.domain.usecase.trip.GetTripUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import org.junit.Before
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

class GalleryViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getTripUseCase: GetTripUseCase
    private lateinit var deleteTripUseCase: DeleteTripUseCase
    private lateinit var getPhotosByTripUseCase: GetPhotosByTripUseCase
    private lateinit var savePhotoUseCase: SavePhotoUseCase
    private lateinit var removePhotoByUriUseCase: RemovePhotoByUriUseCase

    private lateinit var galleryViewModel: GalleryViewModel

    @Before
    fun setup() {
        getTripUseCase = mock()
        deleteTripUseCase = mock()
        getPhotosByTripUseCase = mock()
        savePhotoUseCase = mock()
        removePhotoByUriUseCase = mock()

        galleryViewModel = GalleryViewModel(getTripUseCase, deleteTripUseCase, getPhotosByTripUseCase, savePhotoUseCase, removePhotoByUriUseCase, testDispatcher)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `get all correctly sets the trip and photos`() = runTest {
        val trip = Trip(id = 1L, name = "Test Trip", itinerary = listOf(), date = LocalDate(year = 2024, month = Month.JANUARY, dayOfMonth = 5))
        val photo = Photo(placeId = "1", photoUri = "uri", tripId = trip.id)

        whenever(getTripUseCase.invoke(GetTripUseCase.Params(trip.id))).thenReturn(flowOf(Result.Success(trip)))
        whenever(getPhotosByTripUseCase.invoke(GetPhotosByTripUseCase.Params(trip.id))).thenReturn(flowOf(listOf(photo)))

        galleryViewModel.getAll(trip.id)

        advanceUntilIdle()

        verify(getTripUseCase).invoke(GetTripUseCase.Params(trip.id))
        verify(getPhotosByTripUseCase).invoke(GetPhotosByTripUseCase.Params(trip.id))

        assertEquals(trip, galleryViewModel.lastState().trip)
        assertEquals(listOf(photo), galleryViewModel.lastState().photos)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `get all correctly sets error if get trip fails`() = runTest {
        val trip = Trip(id = 1L, name = "Test Trip", itinerary = listOf(), date = LocalDate(year = 2024, month = Month.JANUARY, dayOfMonth = 5))
        val photo = Photo(placeId = "1", photoUri = "uri", tripId = trip.id)

        whenever(getTripUseCase.invoke(GetTripUseCase.Params(trip.id))).thenReturn(flowOf(Result.Error(TripError.GettingTripError)))
        whenever(getPhotosByTripUseCase.invoke(GetPhotosByTripUseCase.Params(trip.id))).thenReturn(flowOf(listOf(photo)))

        galleryViewModel.getAll(trip.id)

        advanceUntilIdle()

        verify(getTripUseCase).invoke(GetTripUseCase.Params(trip.id))
        verify(getPhotosByTripUseCase).invoke(GetPhotosByTripUseCase.Params(trip.id))

        assertEquals(TripError.GettingTripError, galleryViewModel.errorFlow.first())
        assertEquals(null, galleryViewModel.lastState().trip)
        assertEquals(listOf(photo), galleryViewModel.lastState().photos)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `delete correctly calls delete`() = runTest {
        val trip = Trip(id = 1L, name = "Test Trip", itinerary = listOf(), date = LocalDate(year = 2024, month = Month.JANUARY, dayOfMonth = 5))
        val photo = Photo(placeId = "1", photoUri = "uri", tripId = trip.id)

        whenever(getTripUseCase.invoke(GetTripUseCase.Params(trip.id))).thenReturn(flowOf(Result.Success(trip)))
        whenever(getPhotosByTripUseCase.invoke(GetPhotosByTripUseCase.Params(trip.id))).thenReturn(flowOf(listOf(photo)))

        galleryViewModel.getAll(trip.id)

        advanceUntilIdle()

        galleryViewModel.delete()


        advanceUntilIdle()

        verify(deleteTripUseCase).invoke(trip)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `delete correctly sets error if delete use case returns error`() = runTest {
        val trip = Trip(id = 1L, name = "Test Trip", itinerary = listOf(), date = LocalDate(year = 2024, month = Month.JANUARY, dayOfMonth = 5))
        val photo = Photo(placeId = "1", photoUri = "uri", tripId = trip.id)

        whenever(getTripUseCase.invoke(GetTripUseCase.Params(trip.id))).thenReturn(flowOf(Result.Success(trip)))
        whenever(getPhotosByTripUseCase.invoke(GetPhotosByTripUseCase.Params(trip.id))).thenReturn(flowOf(listOf(photo)))
        whenever(deleteTripUseCase.invoke(trip)).thenReturn(Result.Error(TripError.DeletingTripError))

        galleryViewModel.getAll(trip.id)

        advanceUntilIdle()

        galleryViewModel.delete()

        advanceUntilIdle()

        verify(deleteTripUseCase).invoke(trip)
        assertEquals(TripError.DeletingTripError, galleryViewModel.errorFlow.first())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `delete photo correctly calls remove photo by uri`() = runTest {
        val trip = Trip(id = 1L, name = "Test Trip", itinerary = listOf(), date = LocalDate(year = 2024, month = Month.JANUARY, dayOfMonth = 5))
        val photo = Photo(placeId = "1", photoUri = "uri", tripId = trip.id)

        whenever(getTripUseCase.invoke(GetTripUseCase.Params(trip.id))).thenReturn(flowOf(Result.Success(trip)))
        whenever(getPhotosByTripUseCase.invoke(GetPhotosByTripUseCase.Params(trip.id))).thenReturn(flowOf(listOf(photo)))

        galleryViewModel.getAll(trip.id)

        advanceUntilIdle()

        galleryViewModel.deletePhoto(photo.photoUri)

        advanceUntilIdle()

        verify(removePhotoByUriUseCase).invoke(RemovePhotoByUriUseCase.Params(photo.photoUri))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `delete photo correctly sets error if remove photo by uri use case returns error`() = runTest {
        val trip = Trip(id = 1L, name = "Test Trip", itinerary = listOf(), date = LocalDate(year = 2024, month = Month.JANUARY, dayOfMonth = 5))
        val photo = Photo(placeId = "1", photoUri = "uri", tripId = trip.id)

        whenever(getTripUseCase.invoke(GetTripUseCase.Params(trip.id))).thenReturn(flowOf(Result.Success(trip)))
        whenever(getPhotosByTripUseCase.invoke(GetPhotosByTripUseCase.Params(trip.id))).thenReturn(flowOf(listOf(photo)))
        whenever(removePhotoByUriUseCase.invoke(RemovePhotoByUriUseCase.Params(photo.photoUri))).thenReturn(Result.Error(TripError.DeletingPhotoError))

        galleryViewModel.getAll(trip.id)

        advanceUntilIdle()

        galleryViewModel.deletePhoto(photo.photoUri)

        advanceUntilIdle()

        verify(removePhotoByUriUseCase).invoke(RemovePhotoByUriUseCase.Params(photo.photoUri))
        assertEquals(TripError.DeletingPhotoError, galleryViewModel.errorFlow.first())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `add user photo correctly saves photo`() = runTest {
        val trip = Trip(id = 1L, name = "Test Trip", itinerary = listOf(), date = LocalDate(year = 2024, month = Month.JANUARY, dayOfMonth = 5))
        val photo = Photo(placeId = "1", photoUri = "uri", tripId = trip.id)

        whenever(getTripUseCase.invoke(GetTripUseCase.Params(trip.id))).thenReturn(flowOf(Result.Success(trip)))
        whenever(getPhotosByTripUseCase.invoke(GetPhotosByTripUseCase.Params(trip.id))).thenReturn(flowOf(listOf(photo)))

        galleryViewModel.getAll(trip.id)

        advanceUntilIdle()

        galleryViewModel.currentPlaceId = "1"
        galleryViewModel.addUserPhoto("newPhoto")

        advanceUntilIdle()

        verify(savePhotoUseCase).invoke(Photo(placeId = "1", photoUri = "newPhoto", tripId = trip.id))
        assertEquals("", galleryViewModel.currentPlaceId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
fun `add user photo correctly sets error if save photo use case returns error`() = runTest {
        val trip = Trip(id = 1L, name = "Test Trip", itinerary = listOf(), date = LocalDate(year = 2024, month = Month.JANUARY, dayOfMonth = 5))
        val photo = Photo(placeId = "1", photoUri = "uri", tripId = trip.id)

        whenever(getTripUseCase.invoke(GetTripUseCase.Params(trip.id))).thenReturn(flowOf(Result.Success(trip)))
        whenever(getPhotosByTripUseCase.invoke(GetPhotosByTripUseCase.Params(trip.id))).thenReturn(flowOf(listOf(photo)))
        whenever(savePhotoUseCase.invoke(Photo(placeId = "1", photoUri = "newPhoto", tripId = trip.id))).thenReturn(Result.Error(TripError.SavingPhotoError))

        galleryViewModel.getAll(trip.id)

        advanceUntilIdle()

        galleryViewModel.currentPlaceId = "1"
        galleryViewModel.addUserPhoto("newPhoto")

        advanceUntilIdle()

        verify(savePhotoUseCase).invoke(Photo(placeId = "1", photoUri = "newPhoto", tripId = trip.id))
        assertEquals(TripError.SavingPhotoError, galleryViewModel.errorFlow.first())
        assertEquals("", galleryViewModel.currentPlaceId)
    }
}
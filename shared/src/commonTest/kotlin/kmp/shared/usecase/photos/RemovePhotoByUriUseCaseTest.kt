package kmp.shared.usecase.photos

import kmp.shared.domain.model.Photo
import kmp.shared.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.flowOf
import kmp.shared.base.Result
import kmp.shared.base.error.domain.TripError
import kmp.shared.domain.usecase.photos.RemovePhotoByUriUseCase
import kmp.shared.domain.usecase.photos.RemovePhotoByUriUseCaseImpl
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class RemovePhotoByUriUseCaseTest {
    private val fakeRepository = object : PhotoRepository {
        override suspend fun getPhotos(placeId: String, tripId: Long) = flowOf(emptyList<Photo>())
        override suspend fun getPhotosByTrip(tripId: Long) = flowOf(emptyList<Photo>())
        override suspend fun insertPhoto(photo: Photo) = Result.Success(Unit)
        override suspend fun deletePhotoByTripId(tripId: Long) = Result.Success(Unit)
        override suspend fun deletePhotoByUri(uri: String) = if (uri == "validUri") Result.Success(Unit) else Result.Error(TripError.DeletingPhotoError)
    }

    private val removePhotoByUriUseCase: RemovePhotoByUriUseCase = RemovePhotoByUriUseCaseImpl(fakeRepository)

    @Test
    fun `should remove photo when uri is valid`() = runBlocking {
        // Execute
        val result = removePhotoByUriUseCase(RemovePhotoByUriUseCase.Params("validUri"))

        // Verify
        assertEquals(Result.Success(Unit), result)
    }

    @Test
    fun `should fail to remove photo when uri is invalid`() = runBlocking {
        // Execute
        val result = removePhotoByUriUseCase(RemovePhotoByUriUseCase.Params("invalidUri"))

        // Verify
        assertEquals(Result.Error(TripError.DeletingPhotoError), result)
    }
}
package kmp.shared.usecase.photos

import kmp.shared.base.Result
import kmp.shared.base.error.domain.TripError
import kmp.shared.domain.model.Photo
import kmp.shared.domain.repository.PhotoRepository
import kmp.shared.domain.usecase.photos.SavePhotoUseCase
import kmp.shared.domain.usecase.photos.SavePhotoUseCaseImpl
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class SavePhotoUseCaseTest {
    private val photoRepository = object : PhotoRepository {
        override suspend fun getPhotos(placeId: String, tripId: Long) = flowOf(emptyList<Photo>())
        override suspend fun getPhotosByTrip(tripId: Long) = flowOf(emptyList<Photo>())
        override suspend fun insertPhoto(photo: Photo) = if (photo.photoUri == "validUri") Result.Success(Unit) else Result.Error(TripError.SavingPhotoError)
        override suspend fun deletePhotoByTripId(tripId: Long) = Result.Success(Unit)
        override suspend fun deletePhotoByUri(uri: String) = Result.Success(Unit)
    }

    private val savePhotoUseCase: SavePhotoUseCase = SavePhotoUseCaseImpl(photoRepository)

    @Test
    fun `should return success if repository returns success`() = runBlocking {
        val photo = Photo("place1", 1L, "validUri")
        val result = savePhotoUseCase(photo)

        assertEquals(Result.Success(Unit), result)
    }

    @Test
    fun `should return error if repository returns error`() = runBlocking {
        val photo = Photo("place2", 1L, "invalidUri")
        val result = savePhotoUseCase(photo)

        assertEquals(Result.Error(TripError.SavingPhotoError), result)
    }
}
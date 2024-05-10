package kmp.shared.usecase.photos

import kmp.shared.base.Result
import kmp.shared.domain.model.Photo
import kmp.shared.domain.repository.PhotoRepository
import kmp.shared.domain.usecase.photos.GetPhotosByTripUseCase
import kmp.shared.domain.usecase.photos.GetPhotosByTripUseCaseImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPhotosByTripUseCaseTest {

    fun photos(tripId: Long) = listOf(Photo("1", tripId, "url"))

    private val fakeRepository = object : PhotoRepository {
        override suspend fun getPhotosByTrip(tripId: Long) = if (tripId == 1L) {
            flowOf(photos(tripId))
        } else {
            flowOf(emptyList())
        }
        override suspend fun getPhotos(placeId: String, tripId: Long): Flow<List<Photo>>  = flowOf(emptyList())
        override suspend fun insertPhoto(photo: Photo): Result<Unit> = Result.Success(Unit)
        override suspend fun deletePhotoByTripId(tripId: Long): Result<Unit> = Result.Success(Unit)
        override suspend fun deletePhotoByUri(uri: String): Result<Unit> = Result.Success(Unit)
    }

    private val getPhotosByTripUseCase = GetPhotosByTripUseCaseImpl(fakeRepository)

    @Test
    fun shouldReturnPhotosWhenTripHasPhotos() = runBlocking {
        val tripId = 1L

        val result = mutableListOf<Photo>()
        getPhotosByTripUseCase(GetPhotosByTripUseCase.Params(tripId)).collect { result.addAll(it) }

        assertEquals(photos(tripId), result)
    }

    @Test
    fun shouldReturnEmptyListWhenTripHasNoPhotos() = runBlocking {
        val tripId = 0L

        val result = mutableListOf<Photo>()
        getPhotosByTripUseCase(GetPhotosByTripUseCase.Params(tripId)).collect { result.addAll(it) }

        assertEquals(emptyList(), result)
    }
}
package kmp.shared.usecase.photos

import kmp.shared.base.Result
import kmp.shared.domain.model.Photo
import kmp.shared.domain.repository.PhotoRepository
import kmp.shared.domain.usecase.photos.GetPhotosUseCase
import kmp.shared.domain.usecase.photos.GetPhotosUseCaseImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPhotosUseCaseTest {
    fun photos(tripId: Long, placeId: String) = listOf(Photo(placeId, tripId, "url"))

    private val fakeRepository = object : PhotoRepository {
        override suspend fun getPhotosByTrip(tripId: Long) = flowOf(emptyList<Photo>())
        override suspend fun getPhotos(placeId: String, tripId: Long): Flow<List<Photo>> = if (tripId == 1L && placeId == "place1") {
            flowOf(photos(tripId, placeId))
        } else {
            flowOf(emptyList())
        }
        override suspend fun insertPhoto(photo: Photo): Result<Unit> = Result.Success(Unit)
        override suspend fun deletePhotoByTripId(tripId: Long): Result<Unit> = Result.Success(Unit)
        override suspend fun deletePhotoByUri(uri: String): Result<Unit> = Result.Success(Unit)
    }

    private val getPhotosUseCase: GetPhotosUseCase = GetPhotosUseCaseImpl(fakeRepository)

    @Test
    fun shouldReturnPhotosWhenAvailable() = runBlocking {
        val expectedPhotos = photos(1L, "place1")

        val result = mutableListOf<List<Photo>>()
        getPhotosUseCase(GetPhotosUseCase.Params("place1", 1L)).collect { result.add(it) }

        assertEquals(listOf(expectedPhotos), result)
    }

    @Test
    fun shouldReturnEmptyListWhenNoPhotosAvailable() = runBlocking {
        val result = mutableListOf<List<Photo>>()
        getPhotosUseCase(GetPhotosUseCase.Params("place2", 2L)).collect { result.add(it) }

        assertEquals(listOf(emptyList()), result)
    }

}
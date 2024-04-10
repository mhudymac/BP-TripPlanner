package kmp.shared.domain.usecase.photos

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.model.Photo
import kmp.shared.domain.repository.PhotoRepository

interface SavePhotoUseCase: UseCaseResult<Photo, Unit>

internal class SavePhotoUseCaseImpl internal constructor(
    private val photoRepository: PhotoRepository
) : SavePhotoUseCase {
    override suspend fun invoke(params: Photo): Result<Unit> {
        photoRepository.insertPhoto(params)

        return Result.Success(Unit)
    }
}
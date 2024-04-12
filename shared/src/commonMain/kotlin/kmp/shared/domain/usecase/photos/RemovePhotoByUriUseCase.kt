package kmp.shared.domain.usecase.photos

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.repository.PhotoRepository

interface RemovePhotoByUriUseCase: UseCaseResult<String, Unit>

internal class RemovePhotoByUriUseCaseImpl(
    private val photoRepository: PhotoRepository
): RemovePhotoByUriUseCase {
    override suspend fun invoke(params: String): Result<Unit> {
        photoRepository.deletePhotoByUri(params)
        return Result.Success(Unit)
    }
}
package kmp.shared.domain.usecase.photos

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.repository.PhotoRepository

interface SavePhotoUseCase: UseCaseResult<Triple<String, Long, List<String>>, Unit>

internal class SavePhotoUseCaseImpl internal constructor(
    private val photoRepository: PhotoRepository
) : SavePhotoUseCase {
    override suspend fun invoke(params: Triple<String, Long, List<String>>): Result<Unit> {
        params.third.forEach {
            photoRepository.insertPhoto(params.first, params.second, it)
        }
        return Result.Success(Unit)
    }
}
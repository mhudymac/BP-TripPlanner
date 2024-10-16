package kmp.shared.domain.usecase.photos

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.repository.PhotoRepository

interface RemovePhotoByUriUseCase: UseCaseResult<RemovePhotoByUriUseCase.Params, Unit> {
    data class Params(val uri: String)
}

internal class RemovePhotoByUriUseCaseImpl(
    private val photoRepository: PhotoRepository
): RemovePhotoByUriUseCase {
    override suspend fun invoke(params: RemovePhotoByUriUseCase.Params): Result<Unit> = photoRepository.deletePhotoByUri(params.uri)
}
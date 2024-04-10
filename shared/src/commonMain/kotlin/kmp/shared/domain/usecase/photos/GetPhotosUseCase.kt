package kmp.shared.domain.usecase.photos

import kmp.shared.base.usecase.UseCaseFlow
import kmp.shared.domain.model.Photo
import kmp.shared.domain.repository.PhotoRepository

interface GetPhotosUseCase: UseCaseFlow<Pair<String, Long>, List<Photo>>

internal class GetPhotosUseCaseImpl internal constructor(
    private val photoRepository: PhotoRepository
) : GetPhotosUseCase {
    override suspend fun invoke(params: Pair<String, Long>) = photoRepository.getPhotos(params.first, params.second)
}
package kmp.shared.domain.usecase.photos

import kmp.shared.base.usecase.UseCaseFlow
import kmp.shared.domain.model.Photo
import kmp.shared.domain.repository.PhotoRepository

interface GetPhotosUseCase: UseCaseFlow<GetPhotosUseCase.Params, List<Photo>>{
    data class Params(val placeId: String, val tripId: Long)
}

internal class GetPhotosUseCaseImpl internal constructor(
    private val photoRepository: PhotoRepository
) : GetPhotosUseCase {
    override suspend fun invoke(params: GetPhotosUseCase.Params) = photoRepository.getPhotos(params.placeId, params.tripId)
}
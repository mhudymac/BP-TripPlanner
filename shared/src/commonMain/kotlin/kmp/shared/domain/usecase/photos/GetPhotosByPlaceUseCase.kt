package kmp.shared.domain.usecase.photos

import kmp.shared.base.usecase.UseCaseFlow

interface GetPhotosByPlaceUseCase: UseCaseFlow<String, List<String>>

internal class GetPhotosByPlaceUseCaseImpl internal constructor(
    private val photoRepository: kmp.shared.domain.repository.PhotoRepository
) : GetPhotosByPlaceUseCase {
    override suspend fun invoke(params: String) = photoRepository.getPhotosByPlace(params)
}
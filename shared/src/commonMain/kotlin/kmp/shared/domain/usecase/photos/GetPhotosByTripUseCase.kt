package kmp.shared.domain.usecase.photos

import kmp.shared.base.usecase.UseCaseFlow
import kmp.shared.domain.model.Photo
import kmp.shared.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow

interface GetPhotosByTripUseCase: UseCaseFlow<GetPhotosByTripUseCase.Params, List<Photo>>{
    data class Params(val tripId: Long)
}

internal class GetPhotosByTripUseCaseImpl(
    private val photoRepository: PhotoRepository
): GetPhotosByTripUseCase {
    override suspend fun invoke(params: GetPhotosByTripUseCase.Params): Flow<List<Photo>> = photoRepository.getPhotosByTrip(params.tripId)
}
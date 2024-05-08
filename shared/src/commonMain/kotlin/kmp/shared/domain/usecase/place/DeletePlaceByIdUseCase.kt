package kmp.shared.domain.usecase.place

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.repository.PlaceRepository

interface DeletePlaceByIdUseCase: UseCaseResult<DeletePlaceByIdUseCase.Params, Unit> {
    data class Params(
        val placeId: String,
        val tripId: Long
    )
}

internal class DeletePlaceByIdUseCaseImpl(
    private val placeRepository: PlaceRepository
) : DeletePlaceByIdUseCase {
    override suspend fun invoke(params: DeletePlaceByIdUseCase.Params): Result<Unit> = placeRepository.deleteById(params.placeId, params.tripId)

}
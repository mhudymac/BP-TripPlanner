package kmp.shared.domain.usecase.place

import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.domain.repository.PlaceRepository

interface DeletePlaceByIdUseCase: UseCaseResult<Pair<String, Long>, Unit>

internal class DeletePlaceByIdUseCaseImpl(
    private val placeRepository: PlaceRepository
) : DeletePlaceByIdUseCase {
    override suspend fun invoke(params: Pair<String, Long>): Result<Unit> {
        return placeRepository.deleteById(params.first, params.second)
    }
}
package kmp.shared.domain.usecase.place

import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.base.usecase.UseCaseResult
import kmp.shared.base.util.extension.map
import kmp.shared.domain.model.Place
import kmp.shared.domain.repository.PlaceRepository

interface UpdatePhotoUrlUseCase : UseCaseResult<Place, Place>

internal class UpdatePhotoUrlUseCaseImpl(
    private val repository: PlaceRepository,
) : UpdatePhotoUrlUseCase {
    override suspend fun invoke(params: Place): Result<Place> =
        params.photoName?.let {
            repository.getPhoto(photoName = it).map { url ->
                params.copy(photoUri = url)
            }
        }?: Result.Error(ErrorResult("No photo found"), params)
}
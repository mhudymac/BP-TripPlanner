package kmp.shared.base.usecase

import kotlinx.coroutines.flow.Flow


/**
 * This interface represents a use case in the application that returns a Flow of type T.
 * It is a part of the clean architecture pattern and is used to encapsulate all the business logic of a particular use case.
 *
 * @param Params The type of the parameters that the use case needs to execute.
 * @param T The type of the objects that the use case will return inside a Flow.
 */
interface UseCaseFlow<in Params, out T : Any> {
    suspend operator fun invoke(params: Params): Flow<T>
}

/**
 * This interface represents a use case in the application that returns a Flow of type T and does not require any parameters.
 * It is a part of the clean architecture pattern and is used to encapsulate all the business logic of a particular use case.
 *
 * @param T The type of the objects that the use case will return inside a Flow.
 */
interface UseCaseFlowNoParams<out T : Any> {
    suspend operator fun invoke(): Flow<T>
}

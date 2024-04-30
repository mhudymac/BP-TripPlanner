package kmp.shared.base

/**
 * This sealed class represents a result of a certain operation that can either be successful or result in an error.
 * It is a generic class that can hold any type of data in case of success or an error in case of failure.
 *
 * @param T The type of the data that the result will hold in case of success.
 */
sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()

    data class Error<out T : Any>(val error: ErrorResult, val data: T? = null) : Result<T>()
}

/**
 * This open class represents an error result of an operation.
 * It holds a message that describes the error and a Throwable object that caused the error.
 *
 * @property message The message that describes the error.
 * @property throwable The Throwable object that caused the error.
 */
open class ErrorResult(open var message: String? = null, open var throwable: Throwable? = null)

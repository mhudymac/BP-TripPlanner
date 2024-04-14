package kmp.shared.base.error.domain

import kmp.shared.base.ErrorResult

/**
 * Error type used when handling responses from backend
 * @param throwable optional [Throwable] parameter used for debugging or crash reporting
 */
sealed class TripError(throwable: Throwable? = null) : ErrorResult(throwable = throwable) {
    data object GettingTripError : TripError()

    data object GettingPlaceError : TripError()

    data object SavingTripError : TripError()

    data object DeletingTripError : TripError()

    data object OptimisingTripError : TripError()

    data object UpdatingTripError : TripError()

    data object RepeatingTripError : TripError()

    data object SearchError : TripError()

    data object GettingPhotoError : TripError()

    data object SavingPhotoError : TripError()

    data object DeletingPhotoError : TripError()

    data object GettingDistancesError : TripError()

    data object SavingPlaceError : TripError()

    data object DeletingPlaceError : TripError()

    data object DeletingDistanceError : TripError()

    data object SavingDistanceError : TripError()

    data object NoLocationError : TripError()

    data object TripNameNecessaryError : TripError()

    data object TripDateNecessaryError : TripError()

    data object TripItineraryNecessaryError : TripError()
}
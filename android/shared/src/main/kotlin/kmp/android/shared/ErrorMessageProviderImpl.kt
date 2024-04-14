package kmp.android.shared

import android.content.Context
import kmp.shared.base.ErrorResult
import kmp.shared.base.error.ErrorMessageProvider
import kmp.shared.base.error.domain.BackendError
import kmp.shared.base.error.domain.CommonError
import kmp.shared.base.error.domain.TripError

internal class ErrorMessageProviderImpl(private val context: Context) : ErrorMessageProvider {
    override val defaultMessage: String
        get() = context.getString(R.string.unknown_error)

    override fun ErrorResult.getMessage(defMessage: String): String =
        when (this) {
            is BackendError -> errorMessage
            is CommonError -> errorMessage
            is TripError -> errorMessage
            else -> defMessage
        }

    private val BackendError.errorMessage: String
        get() = when (this) {
            is BackendError.NotAuthorized -> "NotAuthorized - TODO" // TODO authorized error handling/message
        }

    private val TripError.errorMessage: String
        get() =
            when (this) {
                TripError.DeletingTripError -> context.getString(R.string.error_deleting_trip)
                TripError.GettingPlaceError -> context.getString(R.string.error_getting_place)
                TripError.GettingTripError -> context.getString(R.string.error_finding_trip)
                TripError.SavingTripError -> context.getString(R.string.error_saving_trip)
                TripError.OptimisingTripError -> context.getString(R.string.error_optimizing_trip)
                TripError.DeletingDistanceError -> context.getString(R.string.error_deleting_distance)
                TripError.DeletingPhotoError -> context.getString(R.string.error_deleting_photo)
                TripError.DeletingPlaceError -> context.getString(R.string.error_deleting_place)
                TripError.GettingDistancesError -> context.getString(R.string.error_getting_distance)
                TripError.GettingPhotoError -> context.getString(R.string.error_getting_photo)
                TripError.NoLocationError -> context.getString(R.string.error_getting_loaction)
                TripError.RepeatingTripError -> context.getString(R.string.error_repeating_trip)
                TripError.SavingDistanceError -> context.getString(R.string.error_saving_distance)
                TripError.SavingPhotoError -> context.getString(R.string.error_saving_photo)
                TripError.SavingPlaceError -> context.getString(R.string.error_saving_place)
                TripError.SearchError -> context.getString(R.string.error_searching)
                TripError.UpdatingTripError -> context.getString(R.string.error_updating_trip)
                TripError.TripDateNecessaryError -> context.getString(R.string.error_missing_date)
                TripError.TripItineraryNecessaryError -> context.getString(R.string.error_itinerary_missing)
                TripError.TripNameNecessaryError -> context.getString(R.string.error_missing_name)
            }
    private val CommonError.errorMessage: String
        get() =
            when (this) {
                is CommonError.NoNetworkConnection -> context.getString(R.string.error_no_internet_connection)
                CommonError.NoUserLoggedIn -> "No user logged in - TODO" // TODO no user logged in error handling/message
            }

}

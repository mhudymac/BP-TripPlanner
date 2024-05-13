package kmp.android.trip.screens.detail

import com.google.firebase.analytics.FirebaseAnalytics
import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.trip.DeleteTripUseCase
import kmp.shared.domain.usecase.trip.GetTripUseCase
import kmp.shared.domain.usecase.trip.OptimiseTripUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

/**
 * This class represents the ViewModel for the Detail view.
 * It provides functions to get a trip, optimise a trip, and delete a trip.
 */
class DetailViewModel(
    private val getTripByName: GetTripUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
    private val optimiseTripUseCase: OptimiseTripUseCase,
    private val analytics: FirebaseAnalytics
) : BaseStateViewModel<DetailViewModel.ViewState>(ViewState()) {

    private val _errorFlow = MutableSharedFlow<ErrorResult>(replay = 1)
    val errorFlow: Flow<ErrorResult> get() = _errorFlow

    fun getTrip(tripId: Long) {
        launch {
            update { copy(loading = true) }
            getTripByName(GetTripUseCase.Params(tripId)).map {
                when (it) {
                    is Result.Success -> { update { copy(trip = it.data, loading = false, optimisingLoading = false) }}
                    is Result.Error -> { update { copy( loading = false, optimisingLoading = false) }; _errorFlow.emit(it.error) }
                }
            }.collect()

        }
    }

    fun optimise(){
        launch {
            update { copy(optimisingLoading = true) }
            lastState().trip?.let {
                val result = optimiseTripUseCase(it)

                if(result is Result.Error) _errorFlow.emit(result.error)
                else analytics.logEvent("optimised_trip", null)
            }
            update { copy(optimisingLoading = false) }
        }
    }

    fun delete(){
        launch {
            lastState().trip?.let {
                val result = deleteTripUseCase(it)

                if(result is Result.Error) _errorFlow.emit(result.error)
            }
        }

    }

    data class ViewState (
        val trip: Trip? = null,
        val loading: Boolean = false,
        val optimisingLoading: Boolean = false,
    ) : State
}

package kmp.android.trip.ui.home

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.Result
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.trip.GetNearestTripUseCase

class HomeViewModel(
    private val getNearestTripUseCase: GetNearestTripUseCase
) : BaseStateViewModel<HomeViewModel.ViewState>(ViewState()) {


    init {
        launch {
            loading = true
            getNearestTripUseCase().collect { result ->
                when (result) {
                    is Result.Success -> {
                        update { copy(trip = result.data) }
                    }
                    is Result.Error -> {
                        update { copy(error = result.error.message?: "An error occurred") }
                    }
                }
            }
            loading = false
        }
    }

    var loading
        get() = lastState().loading
        set(value) { update { copy(loading = value) } }

     data class ViewState(
         val trip: Trip? = null,
         val loading: Boolean = false,
         val error: String = ""
     ) : State
}
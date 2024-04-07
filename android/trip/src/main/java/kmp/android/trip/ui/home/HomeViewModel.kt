package kmp.android.trip.ui.home

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.Result
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.distances.GetDistancesUseCase
import kmp.shared.domain.usecase.trip.GetNearestTripUseCase

class HomeViewModel(
    private val getNearestTripUseCase: GetNearestTripUseCase,
    private val getDistancesUseCase: GetDistancesUseCase
) : BaseStateViewModel<HomeViewModel.ViewState>(ViewState()) {


    init {
        launch {
            loading = true
            getNearestTripUseCase().collect { result ->
                when (result) {
                    is Result.Success -> {
                        if(result.data.distances.isEmpty()){
                            val trip = getDistancesUseCase(result.data)
                            when(trip){
                                is Result.Success -> {
                                    update { copy(trip = trip.data) }
                                }
                                is Result.Error -> {
                                    update { copy(error = trip.error.message?: "An error occurred") }
                                }
                            }
                        } else {
                            update { copy(trip = result.data) }
                        }
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
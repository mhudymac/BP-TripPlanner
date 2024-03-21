package kmp.android.trip.vm

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.trip.GetAllTripsUseCase


class ListViewModel(
    private val getAllTripsUseCase: GetAllTripsUseCase
) : BaseStateViewModel<ListViewModel.ViewState>(ViewState()) {

    init {
        loadTrips()
    }

    private fun loadTrips() {
        launch {
            update { copy(isLoading = true) }
            getAllTripsUseCase().collect { trips ->
                update { copy(trips = trips, isLoading = false) }
            }
        }
    }

    var loading: Boolean
        get() = lastState().isLoading
        set(value) { update { copy(isLoading = value) } }

    data class ViewState(
        val isLoading: Boolean = false,
        val trips: List<Trip> = emptyList()

    ) : State

}



package kmp.android.trip.ui.list

import androidx.compose.runtime.mutableIntStateOf
import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.trip.GetCompletedTripsWithoutPlacesUseCase
import kmp.shared.domain.usecase.trip.GetUncompletedTripsWithoutPlacesUseCase


class ListViewModel(
    private val getUncompletedTripsWithoutPlacesUseCase: GetUncompletedTripsWithoutPlacesUseCase,
    private val getCompletedTripsWithoutPlacesUseCase: GetCompletedTripsWithoutPlacesUseCase
) : BaseStateViewModel<ListViewModel.ViewState>(ViewState()) {

    init {
        loadTrips()
    }

    private fun loadTrips() {
        launch {
            loading = true
            loadUncompleted()
            loadCompleted()
        }
    }

    private fun loadUncompleted() {
        launch {
            update { copy(isLoading = true) }
            getUncompletedTripsWithoutPlacesUseCase().collect { trips ->
                update { copy(uncompletedTrips = trips, isLoading = false) }
                loading = false
            }
        }
    }

    private fun loadCompleted() {
        launch {
            update { copy(isLoading = true) }
            getCompletedTripsWithoutPlacesUseCase().collect { trips ->
                update { copy(completedTrips = trips, isLoading = false) }
                loading = false
            }
        }
    }

    private var loading: Boolean
        get() = lastState().isLoading
        set(value) { update { copy(isLoading = value) } }

    var selectedTab: Int
        get() = lastState().selectedTab
        set(value) { update { copy(selectedTab = value) } }

    data class ViewState(
        val isLoading: Boolean = false,
        val uncompletedTrips: List<Trip> = emptyList(),
        val completedTrips: List<Trip> = emptyList(),
        val selectedTab: Int = 0,
    ) : State

}



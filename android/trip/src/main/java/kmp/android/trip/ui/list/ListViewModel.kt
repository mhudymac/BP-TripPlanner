package kmp.android.trip.ui.list

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.Result
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.trip.GetCompletedTripsWithoutPlacesUseCase
import kmp.shared.domain.usecase.trip.GetUncompletedTripsWithoutPlacesUseCase
import kmp.shared.domain.usecase.trip.RepeatTripUseCase
import kmp.shared.domain.usecase.trip.UpdateTripDateUseCase
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate


class ListViewModel(
    private val getUncompletedTripsWithoutPlacesUseCase: GetUncompletedTripsWithoutPlacesUseCase,
    private val getCompletedTripsWithoutPlacesUseCase: GetCompletedTripsWithoutPlacesUseCase,
    private val repeatTripUseCase: RepeatTripUseCase,
    private val updateTripDate: UpdateTripDateUseCase
) : BaseStateViewModel<ListViewModel.ViewState>(ViewState()) {

    init {
        loadTrips()
    }

    private fun loadTrips() {
        launch {
            loadUncompleted()
            loadCompleted()
        }
    }

    private fun loadUncompleted() {
        launch {
            update { copy(loadingUpcoming = true) }
            getUncompletedTripsWithoutPlacesUseCase().collect { trips ->
                update { copy(uncompletedTrips = trips, loadingUpcoming = false) }
            }
        }
    }

    private fun loadCompleted() {
        launch {
            update { copy(loadingCompleted = true) }
            getCompletedTripsWithoutPlacesUseCase().collect { trips ->
                update { copy(completedTrips = trips, loadingCompleted = false) }
            }
        }
    }

    fun repeatTrip(trip: Trip) {
        launch {
            when(val result = repeatTripUseCase(trip)) {
                is Result.Success -> update { copy(editId = result.data) }
                is Result.Error -> update { copy(error = result.error.message?: "Error") }
            }
        }
    }

    fun startTrip(trip: Trip) {
        launch {
            updateTripDate(trip.copy(date = LocalDate.now().toKotlinLocalDate()))
        }
    }

    fun clearEdit() {
        update { copy(editId = null) }
    }

    var selectedTab: Int
        get() = lastState().selectedTab
        set(value) { update { copy(selectedTab = value) } }

    data class ViewState(
        val loadingUpcoming: Boolean = false,
        val loadingCompleted: Boolean = false,
        val uncompletedTrips: List<Trip> = emptyList(),
        val completedTrips: List<Trip> = emptyList(),
        val selectedTab: Int = 0,
        val editId: Long? = null,
        val error: String = ""
    ) : State

}



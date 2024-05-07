package kmp.android.trip.screens.list

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.trip.GetCompletedTripsWithoutPlacesUseCase
import kmp.shared.domain.usecase.trip.GetUncompletedTripsWithoutPlacesUseCase
import kmp.shared.domain.usecase.trip.RepeatTripUseCase
import kmp.shared.domain.usecase.trip.UpdateOnlyTripDetailsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate

/**
 * This class represents the ViewModel for the List view.
 * It provides functions to load trips, repeat a trip, start a trip, and clear the edit state.
 */
class ListViewModel(
    private val getUncompletedTripsWithoutPlacesUseCase: GetUncompletedTripsWithoutPlacesUseCase,
    private val getCompletedTripsWithoutPlacesUseCase: GetCompletedTripsWithoutPlacesUseCase,
    private val repeatTripUseCase: RepeatTripUseCase,
    private val updateTripDate: UpdateOnlyTripDetailsUseCase
) : BaseStateViewModel<ListViewModel.ViewState>(ViewState()) {

    private val _errorFlow = MutableSharedFlow<ErrorResult>(replay = 1)
    val errorFlow: Flow<ErrorResult> get() = _errorFlow

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
                is Result.Error -> _errorFlow.emit(result.error)
            }
        }
    }

    fun startTrip(trip: Trip) {
        launch {
            val result = updateTripDate(trip.copy(date = LocalDate.now().toKotlinLocalDate()))
            if(result is Result.Error) _errorFlow.emit(result.error)
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
    ) : State

}



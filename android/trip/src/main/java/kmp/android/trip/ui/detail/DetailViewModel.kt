package kmp.android.trip.ui.detail

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.Result
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.trip.GetTripUseCase
import kmp.shared.system.Log
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class DetailViewModel(
    private val getTripByName: GetTripUseCase
) : BaseStateViewModel<DetailViewModel.ViewState>(ViewState()) {



    fun getTrip(tripId: Long) {
        launch {
            update { copy(loading = true) }
            getTripByName(tripId).map {
                when (it) {
                    is Result.Success -> { update { copy(trip = it.data) }}
                    is Result.Error -> { update { copy(error = it.error.message?: "") }}
                }
            }.collect()

            update { copy(loading = false) }
        }
    }

    data class ViewState (
        val trip: Trip? = null,
        val loading: Boolean = false,
        val error: String = ""
    ) : State
}

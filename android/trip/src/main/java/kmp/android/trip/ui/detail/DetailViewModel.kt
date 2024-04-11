package kmp.android.trip.ui.detail

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.Result
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.trip.DeleteTripUseCase
import kmp.shared.domain.usecase.trip.GetTripUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class DetailViewModel(
    private val getTripByName: GetTripUseCase,
    private val deleteTripUseCase: DeleteTripUseCase
) : BaseStateViewModel<DetailViewModel.ViewState>(ViewState()) {



    fun getTrip(tripId: Long) {
        launch {
            update { copy(loading = true) }
            getTripByName(tripId).map {
                when (it) {
                    is Result.Success -> { update { copy(trip = it.data, loading = false) }}
                    is Result.Error -> { update { copy(error = it.error.message?: "Trip wasn't found", loading = false) }}
                }
            }.collect()

        }
    }

    fun delete(){
        launch {
            lastState().trip?.let {
                deleteTripUseCase(it)
            }
        }

    }

    data class ViewState (
        val trip: Trip? = null,
        val loading: Boolean = false,
        val error: String = ""
    ) : State
}

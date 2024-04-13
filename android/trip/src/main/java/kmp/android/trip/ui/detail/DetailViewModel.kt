package kmp.android.trip.ui.detail

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.Result
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.trip.DeleteTripUseCase
import kmp.shared.domain.usecase.trip.GetTripUseCase
import kmp.shared.domain.usecase.trip.OptimiseTripUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class DetailViewModel(
    private val getTripByName: GetTripUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
    private val optimiseTripUseCase: OptimiseTripUseCase
) : BaseStateViewModel<DetailViewModel.ViewState>(ViewState()) {



    fun getTrip(tripId: Long) {
        launch {
            update { copy(loading = true) }
            getTripByName(tripId).map {
                when (it) {
                    is Result.Success -> { update { copy(trip = it.data, loading = false, optimisingLoading = false) }}
                    is Result.Error -> { update { copy(error = it.error.message?: "Trip wasn't found", loading = false, optimisingLoading = false) }}
                }
            }.collect()

        }
    }

    fun optimise(){
        launch {
            update { copy(optimisingLoading = true) }
            lastState().trip?.let {
                val result = optimiseTripUseCase(it)

                if(result is Result.Error){
                    update { copy(error = result.error.message?: "Error optimising trip") }
                }
            }
            update { copy(optimisingLoading = false) }
        }
    }

    fun delete(){
        launch {
            lastState().trip?.let {
                val result = deleteTripUseCase(it)

                if(result is Result.Error){
                    update { copy(error = result.error.message?: "Error deleting trip") }
                }
            }
        }

    }

    data class ViewState (
        val trip: Trip? = null,
        val loading: Boolean = false,
        val optimisingLoading: Boolean = false,
        val error: String = ""
    ) : State
}

package kmp.android.trip.ui.home

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.Result
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.location.GetLocationFlowUseCase
import kmp.shared.domain.usecase.trip.GetNearestTripUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class HomeViewModel(
    private val getNearestTripUseCase: GetNearestTripUseCase,
    private val getLocationFlowUseCase: GetLocationFlowUseCase,
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
                        update { copy(error = result.error.message ?: "An error occurred") }
                    }
                }
            }
            loading = false
        }
    }

    suspend fun getLocationFlow(): Flow<Location> = getLocationFlowUseCase()

    fun getDistanceBetween(location1: Location, location2: Location): Int {
        val distanceInMeters = FloatArray(1)

        android.location.Location.distanceBetween(
            location1.latitude,
            location1.longitude,
            location2.latitude,
            location2.longitude,
            distanceInMeters
        )

        return distanceInMeters[0].toInt()
    }

    var loading
        get() = lastState().loading
        set(value) { update { copy(loading = value) } }

     data class ViewState(
         val trip: Trip? = null,
         val loading: Boolean = false,
         val error: String = "",
         val currentPlace: String = "",
     ) : State
}
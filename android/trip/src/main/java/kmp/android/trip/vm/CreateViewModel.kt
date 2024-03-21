package kmp.android.trip.vm

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.trip.SaveTripUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime


import kotlin.random.Random

class CreateViewModel(
    private val saveTripUseCase: SaveTripUseCase
) : BaseStateViewModel<CreateViewModel.ViewState>(ViewState()) {

    fun addPlace(place: Place) {
        if( lastState().start == null ) update { copy(start = place) }
        else update { copy(itinerary = itinerary + place) }
    }

    fun updateName(name: String) {
        update { copy(name = name) }
    }

    fun updateDate(date: LocalDateTime) {
        update { copy(date = date) }
    }

    fun saveTrip() {
        launch {
            if( lastState().name.isEmpty() )
                update { copy(error = Pair("Name is required", Random.nextInt())) }
            else if( lastState().date == null )
                update { copy(error = Pair("Date is required", Random.nextInt())) }
            else if( lastState().start == null )
                update { copy(error = Pair("Start place is required", Random.nextInt())) }
            else if( lastState().itinerary.isEmpty() )
                update { copy(error = Pair("At least one place is required", Random.nextInt())) }
            else{
                val trip = lastState().let { state ->
                    Trip(
                        name = state.name,
                        date = state.date!!.toKotlinLocalDateTime(),
                        itinerary = flowOf(state.itinerary),
                    )
                }

                saveTripUseCase(trip)
            }
        }
    }

    var loading: Boolean
        get() = lastState().isLoading
        set(value) { update { copy(isLoading = value) } }

    data class ViewState(
        val name: String = "",
        val date: LocalDateTime? = null,
        val start: Place? = null,
        val itinerary: List<Place> = emptyList(),
        val isLoading: Boolean = false,
        val error: Pair<String,Int> = Pair("",0)
    ) : State

}
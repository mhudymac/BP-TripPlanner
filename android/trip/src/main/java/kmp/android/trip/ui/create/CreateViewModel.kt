package kmp.android.trip.ui.create

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.Result
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.location.GetLocationFlowUseCase
import kmp.shared.domain.usecase.location.GetLocationUseCase
import kmp.shared.domain.usecase.place.GetPlaceByLocationUseCase
import kmp.shared.domain.usecase.trip.SaveTripWithoutIdUseCase
import kmp.shared.system.Log
import kotlinx.coroutines.flow.first
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate


import kotlin.random.Random

class CreateViewModel(
    private val saveTripUseCase: SaveTripWithoutIdUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val getPlaceByLocationUseCase: GetPlaceByLocationUseCase
) : BaseStateViewModel<CreateViewModel.ViewState>(ViewState()) {

    fun addPlace(place: Place) {
        if( lastState().start == null ) update { copy(start = place) }
        else update { copy(itinerary = itinerary + place) }

        Log.d("CreateViewModel", "addPlace: ${lastState().itinerary}")
    }

    fun updateName(name: String) {
        update { copy(name = name) }
    }

    fun updateDate(date: LocalDate) {
        update { copy(date = date) }
    }

    fun getLocation() {
        launch {
            loading = true
            when(val location = getLocationUseCase()){
                is Result.Success -> {
                    when(val place = getPlaceByLocationUseCase(location.data)){
                        is Result.Success -> addPlace(place.data)
                        is Result.Error -> update { copy(error = Pair("Error getting address", Random.nextInt())) }
                    }
                }
                is Result.Error -> update { copy(error = Pair("Error getting location", Random.nextInt())) }
            }
            loading = false
        }
    }

    fun saveTrip() {
        launch {
            loading = true
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
                        id = 0,
                        name = state.name,
                        date = state.date!!.toKotlinLocalDate(),
                        itinerary = listOf(state.start!!) + state.itinerary,
                        order = (listOf(state.start) + state.itinerary).map { it.id },
                    )
                }

                saveTripUseCase(trip)
                update{ copy(saveSuccess = true) }
            }
            loading = false
        }
    }

    var loading: Boolean
        get() = lastState().isLoading
        set(value) { update { copy(isLoading = value) } }

    data class ViewState(
        val name: String = "",
        val date: LocalDate? = null,
        val start: Place? = null,
        val itinerary: List<Place> = emptyList(),
        val isLoading: Boolean = false,
        val error: Pair<String,Int> = Pair("",0),
        val saveSuccess: Boolean = false
    ) : State

}
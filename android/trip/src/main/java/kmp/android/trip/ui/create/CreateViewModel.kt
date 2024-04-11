package kmp.android.trip.ui.create

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.Result
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.location.GetLocationFlowUseCase
import kmp.shared.domain.usecase.location.GetLocationUseCase
import kmp.shared.domain.usecase.place.DeletePlaceByIdUseCase
import kmp.shared.domain.usecase.place.GetPlaceByLocationUseCase
import kmp.shared.domain.usecase.trip.GetTripUseCase
import kmp.shared.domain.usecase.trip.SaveTripUseCase
import kmp.shared.domain.usecase.trip.SaveTripWithoutIdUseCase
import kmp.shared.system.Log
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate


import kotlin.random.Random

class CreateViewModel(
    private val getTripById: GetTripUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val getPlaceByLocationUseCase: GetPlaceByLocationUseCase,
    private val saveTripWithoutIdUseCase: SaveTripWithoutIdUseCase,
    private val saveTripUseCase: SaveTripUseCase,
    private val deletePlaceUseCase: DeletePlaceByIdUseCase
    ) : BaseStateViewModel<CreateViewModel.ViewState>(ViewState()) {

    private var id: Long = -1
    private var wasPlaceAdded = false
    fun getTrip(tripId: Long) {
        launch {
            loading = true
            getTripById(tripId).map {
                when (it) {
                    is Result.Success -> { update { copy(
                        name = it.data.name,
                        date = it.data.date.toJavaLocalDate(),
                        itinerary = it.data.itinerary,
                    ) }
                        id = it.data.id
                    }
                    is Result.Error -> { update { copy(error = Pair(it.error.message?: "Error getting place", Random.nextInt())) }}
                }
            }.collect()
            loading = false
        }
    }

    fun addPlace(place: Place) {
        update { copy(itinerary = itinerary + place) }
    }

    fun removePlace(place: Place) {
        if(id != -1L) {
            launch {
                deletePlaceUseCase(Pair(place.id, id))
            }
        }
        update { copy(itinerary = itinerary - place) }
    }

    fun updateName(name: String) {
        update { copy(name = name) }
    }

    fun updateDate(date: LocalDate) {
        update { copy(date = date) }
    }

    fun updateTripOrder(list: List<Place>) {
        update { copy(itinerary = list) }
    }

    fun toggleReordering() {
        update { copy(reordering = !reordering) }
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
            else if( lastState().itinerary.isEmpty() )
                update { copy(error = Pair("At least two places are required", Random.nextInt())) }
            else{
                val trip = lastState().let { state ->
                    Trip(
                        id = id,
                        name = state.name,
                        date = state.date!!.toKotlinLocalDate(),
                        itinerary = state.itinerary,
                        order = state.itinerary.map { it.id },
                    )
                }

                if(id == -1L){
                    saveTripWithoutIdUseCase(trip)
                    update{ copy(saveSuccess = true) }
                } else {
                    when(val saved = saveTripUseCase(Pair(trip, wasPlaceAdded))) {
                        is Result.Success -> update { copy(saveSuccess = true) }
                        is Result.Error -> update { copy(error = Pair(saved.error.message ?: "An error occurred while saving", Random.nextInt())) }
                    }
                }
            }
            loading = false
        }
    }

    var loading: Boolean
        get() = lastState().loading
        set(value) { update { copy(loading = value) } }

    data class ViewState(
        val name: String = "",
        val date: LocalDate? = null,
        val itinerary: List<Place> = emptyList(),
        val loading: Boolean = false,
        val error: Pair<String,Int> = Pair("",0),
        val reordering: Boolean = false,
        val saveSuccess: Boolean = false
    ) : State

}
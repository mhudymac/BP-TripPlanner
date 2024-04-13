package kmp.android.trip.ui.edit

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.Result
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.place.GetPlaceByLocationUseCase
import kmp.shared.domain.usecase.trip.DeleteTripUseCase
import kmp.shared.domain.usecase.trip.GetTripUseCase
import kmp.shared.domain.usecase.trip.SaveTripUseCase
import kmp.shared.domain.usecase.trip.SaveTripWithoutIdUseCase
import kotlinx.coroutines.flow.first
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate


import kotlin.random.Random

class EditViewModel(
    private val getTripById: GetTripUseCase,
    private val getPlaceByLocationUseCase: GetPlaceByLocationUseCase,
    private val saveTripWithoutIdUseCase: SaveTripWithoutIdUseCase,
    private val saveTripUseCase: SaveTripUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
    private val saveAndOptimiseTripWithoutIdUseCase: SaveTripWithoutIdUseCase,
    ) : BaseStateViewModel<EditViewModel.ViewState>(ViewState()) {

    private var id: Long = -1
    private var wasItineraryChanged = false
    fun getTrip(tripId: Long) {
        if(tripId == -1L) return
        launch {
            update { copy(screenLoading = true) }
            when(val trip = getTripById(tripId).first()){
                is Result.Success -> {
                    if (trip.data.id == 0L) {
                        update {
                            copy(itinerary = trip.data.itinerary)
                        }
                        deleteTripUseCase(trip.data)
                    } else {
                        update {
                            copy(
                                name = trip.data.name,
                                date = trip.data.date.toJavaLocalDate(),
                                itinerary = trip.data.itinerary,
                            )
                        }
                        id = trip.data.id
                    }
                }
                is Result.Error -> update { copy(error = Pair(trip.error.message?: "Error getting place", Random.nextInt())) }
            }
            update { copy(screenLoading = false) }
        }
    }

    fun addPlace(place: Place) {
        wasItineraryChanged = true
        update { copy(itinerary = itinerary + place) }
    }

    fun removePlace(place: Place) {
        wasItineraryChanged = true
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
            update { copy(locationLoading = true) }
            when(val place = getPlaceByLocationUseCase()){
                is Result.Success -> addPlace(place.data)
                is Result.Error -> update { copy(error = Pair("Error getting address", Random.nextInt())) }
            }
            update { copy(locationLoading = true) }
        }
    }
    fun saveTrip(optimise: Boolean) {
        update { copy(savingLoading = true) }
        launch {
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

                if (id == -1L) {
                    when(val result = saveTripWithoutIdUseCase(Pair(trip, optimise))){
                        is Result.Success -> update { copy(saveSuccess = true) }
                        is Result.Error -> update { copy(error = Pair(result.error.message ?: "An error occurred while saving", Random.nextInt())) }
                    }
                } else {
                    when (val result = saveTripUseCase(Pair(trip, wasItineraryChanged))) {
                        is Result.Success -> update { copy(saveSuccess = true) }
                        is Result.Error -> update { copy(error = Pair(result.error.message ?: "An error occurred while saving", Random.nextInt())) }
                    }
                }

            }
            update { copy(savingLoading = false) }
        }
    }

    data class ViewState(
        val name: String = "",
        val date: LocalDate? = null,
        val itinerary: List<Place> = emptyList(),
        val locationLoading: Boolean = false,
        val savingLoading: Boolean = false,
        val screenLoading: Boolean = false,
        val error: Pair<String,Int> = Pair("",0),
        val reordering: Boolean = false,
        val saveSuccess: Boolean = false
    ) : State

}
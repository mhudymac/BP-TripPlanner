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
import kmp.android.shared.R
import kmp.shared.base.error.domain.TripError


import kotlin.random.Random

/**
 * This class represents the ViewModel for the Edit view.
 * It provides functions to get a trip by its ID, add a place to a trip, remove a place from a trip,
 * update the name of a trip, update the date of a trip, update the order of places in a trip,
 * toggle the reordering of places in a trip, get the location of a place, and save a trip.
 */
class EditViewModel(
    private val getTripById: GetTripUseCase,
    private val getPlaceByLocationUseCase: GetPlaceByLocationUseCase,
    private val saveTripWithoutIdUseCase: SaveTripWithoutIdUseCase,
    private val saveTripUseCase: SaveTripUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
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
                is Result.Error -> update { copy(error = Pair(trip.error.message?: "", Random.nextInt())) }
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
                is Result.Error -> update { copy(error = Pair(place.error.message?: "", Random.nextInt())) }
            }
            update { copy(locationLoading = false) }
        }
    }
    fun saveTrip(optimise: Boolean) {
        update { copy(savingLoading = true) }
        launch {
            if( lastState().name.isEmpty() )
                update { copy(error = Pair(TripError.TripNameNecessaryError.message!!, Random.nextInt())) }
            else if( lastState().date == null )
                update { copy(error = Pair(TripError.TripDateNecessaryError.message!!, Random.nextInt())) }
            else if( lastState().itinerary.isEmpty() )
                update { copy(error = Pair(TripError.TripItineraryNecessaryError.message!!, Random.nextInt())) }
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
                        is Result.Error -> update { copy(error = Pair(result.error.message ?: "", Random.nextInt())) }
                    }
                } else {
                    when (val result = saveTripUseCase(Pair(trip, wasItineraryChanged))) {
                        is Result.Success -> update { copy(saveSuccess = true) }
                        is Result.Error -> update { copy(error = Pair(result.error.message ?: "", Random.nextInt())) }
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
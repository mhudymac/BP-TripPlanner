package kmp.android.trip.ui.home

import android.net.Uri
import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.domain.model.Photo
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.photos.SavePhotoUseCase
import kmp.shared.domain.usecase.trip.GetNearestTripUseCase
import kmp.shared.domain.usecase.trip.UpdateTripDateUseCase
import kmp.shared.system.Log
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate

class HomeViewModel(
    private val getNearestTripUseCase: GetNearestTripUseCase,
    private val updateTripDateUseCase: UpdateTripDateUseCase,
    private val savePhotosUseCase: SavePhotoUseCase
) : BaseStateViewModel<HomeViewModel.ViewState>(ViewState()) {



    init {
        launch {
            loading = true
            getNearestTripUseCase().collect { result ->
                update { copy(
                    trips = result,
                    trip = trip?.id?.let{ it1 -> result.firstOrNull { it2 -> it1 == it2.id }}?: result.firstOrNull(),
                    isActive = result.firstOrNull()?.date == LocalDate.now().toKotlinLocalDate(),
                    loading = false
                )}
            }
        }
    }

    fun startTrip() {
        val trip = lastState().trip?.copy(date = LocalDate.now().toKotlinLocalDate())
        if(trip != null) {
            update { copy(trip = trip, isActive = true) }
            launch {
                updateTripDateUseCase(trip)
            }
        }
    }

    fun finishTrip() {
        val trip = lastState().trip?.copy(completed = true)
        if(trip != null) {
            launch {
                updateTripDateUseCase(trip)
            }
        }
    }

    fun addUserPhoto(photoUri: String) {
        launch {
            val trip = lastState().trip
            if(trip != null)
                savePhotosUseCase(Photo(trip.activePlace, trip.id, photoUri))
        }
    }

    fun setActiveTrip(trip: Trip) {
        update { copy(trip = trip) }
    }

    var loading
        get() = lastState().loading
        set(value) { update { copy(loading = value) } }

     data class ViewState(
         val trips: List<Trip> = emptyList(),
         val trip: Trip? = null,
         val loading: Boolean = true,
         val error: String = "",
         val isActive: Boolean = false,
         val selectedTab: Int = 0
     ) : State
}
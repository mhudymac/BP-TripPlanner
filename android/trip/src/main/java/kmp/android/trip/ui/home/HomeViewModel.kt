package kmp.android.trip.ui.home

import android.net.Uri
import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.Result
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Photo
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.location.GetLocationFlowUseCase
import kmp.shared.domain.usecase.photos.GetPhotosUseCase
import kmp.shared.domain.usecase.photos.SavePhotoUseCase
import kmp.shared.domain.usecase.trip.GetNearestTripUseCase
import kmp.shared.domain.usecase.trip.UpdateTripDateUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate

class HomeViewModel(
    private val getNearestTripUseCase: GetNearestTripUseCase,
    private val updateTripDateUseCase: UpdateTripDateUseCase,
    private val getLocationFlowUseCase: GetLocationFlowUseCase,
    private val savePhotosUseCase: SavePhotoUseCase,
    private val getPhotosUseCase: GetPhotosUseCase
) : BaseStateViewModel<HomeViewModel.ViewState>(ViewState()) {

    var activePlaceId: String = ""
        set(value) {
            field = value
            val tripId = lastState().trip?.id
            if(value.isNotEmpty() && tripId != null) {
                launch {
                    getPhotosUseCase(Pair(value, tripId)).collect { photos ->
                        update { copy(images = photos.map { Uri.parse(it.photoUri) }) }
                    }

                }
            }
        }

    init {
        launch {
            loading = true
            getNearestTripUseCase().collect { result ->
                when (result) {
                    is Result.Success -> {
                        update { copy(trip = result.data, isActive = result.data.date == LocalDate.now().toKotlinLocalDate(), loading = false) }
                    }
                    is Result.Error -> {
                        update { copy(trip = null, error = result.error.message ?: "No saved trip", loading = false) }
                    }
                }
            }
        }
    }

    suspend fun getLocationFlow(): Flow<Location> = getLocationFlowUseCase()

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
            val tripId = lastState().trip?.id
            if(activePlaceId.isNotEmpty() && tripId != null)
                savePhotosUseCase(Photo(activePlaceId, tripId, photoUri))
        }
        update { copy(images = images + Uri.parse(photoUri)) }
    }

    var loading
        get() = lastState().loading
        set(value) { update { copy(loading = value) } }

     data class ViewState(
         val trip: Trip? = null,
         val loading: Boolean = true,
         val error: String = "",
         val images: List<Uri> = emptyList(),
         val isActive: Boolean = false
     ) : State
}
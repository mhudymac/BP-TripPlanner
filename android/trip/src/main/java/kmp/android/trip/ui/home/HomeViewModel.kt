package kmp.android.trip.ui.home

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.Result
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.location.GetLocationFlowUseCase
import kmp.shared.domain.usecase.photos.GetPhotosByPlaceUseCase
import kmp.shared.domain.usecase.photos.SavePhotoUseCase
import kmp.shared.domain.usecase.trip.GetNearestTripUseCase
import kotlinx.coroutines.flow.Flow

class HomeViewModel(
    private val getNearestTripUseCase: GetNearestTripUseCase,
    private val getLocationFlowUseCase: GetLocationFlowUseCase,
    private val savePhotosUseCase: SavePhotoUseCase,
    private val getPhotosByPlaceUseCase: GetPhotosByPlaceUseCase
) : BaseStateViewModel<HomeViewModel.ViewState>(ViewState()) {

    var activePlaceId: String = ""
        set(value) {
            field = value
            if(value.isNotEmpty()) {
                launch {
                    getPhotosByPlaceUseCase(value).collect { photos ->
                        update { copy(images = photos.map { Uri.parse(it) }) }
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
                        update { copy(trip = result.data) }
                    }
                    is Result.Error -> {
                        update { copy(error = result.error.message ?: "No saved trip") }
                    }
                }
                loading = false
            }
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

    fun addUserPhoto(photoUri: String) {
        launch {
            val tripId = lastState().trip?.id
            if(activePlaceId.isNotEmpty() && tripId != null)
                savePhotosUseCase(Triple(activePlaceId, tripId, listOf(photoUri)))
        }
        update { copy(images = images + Uri.parse(photoUri)) }
    }

    var loading
        get() = lastState().loading
        set(value) { update { copy(loading = value) } }

     data class ViewState(
         val trip: Trip? = null,
         val loading: Boolean = false,
         val error: String = "",
         val images: List<Uri> = emptyList()
     ) : State
}
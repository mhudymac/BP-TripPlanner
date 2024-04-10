package kmp.android.trip.ui.gallery

import android.net.Uri
import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.trip.GetTripUseCase
import kmp.shared.base.Result
import kmp.shared.domain.model.Photo
import kmp.shared.domain.usecase.photos.GetPhotosByTripUseCase
import kmp.shared.domain.usecase.photos.SavePhotoUseCase

internal class GalleryViewModel(
    private val getTripUseCase: GetTripUseCase,
    private val getPhotosByTripUseCase: GetPhotosByTripUseCase,
    private val savePhotoUseCase: SavePhotoUseCase
) : BaseStateViewModel<GalleryViewModel.ViewState>(ViewState()) {

    var currentPlaceId: String = ""

     private fun getTrip(tripId: Long) {
          launch {
              update { copy(loading = true) }
              getTripUseCase(tripId).collect {
                    when (it) {
                         is Result.Success -> { update { copy(trip = it.data) }}
                         is Result.Error -> { update { copy(error = it.error.message?: "Trip wasn't found") }}
                    }
                    update { copy(loading = false) }
                }
          }
     }

    private fun getPhotos(tripId: Long) {
        launch {
            getPhotosByTripUseCase(tripId).collect { photos ->
                update { copy(photos = photos) }
            }
        }
    }

    fun getAll(tripId: Long) {
        getTrip(tripId)
        getPhotos(tripId)
    }

    fun addUserPhoto(photo: String) {
        val tripId = lastState().trip?.id

        if(currentPlaceId.isNotEmpty() && tripId != null) {
            val newPhoto = Photo(placeId = currentPlaceId, tripId = tripId, photoUri = photo)

            launch {
                savePhotoUseCase( newPhoto )
            }
        }

        currentPlaceId = ""
    }

    var editing: Boolean
        get() = lastState().editing
        set(value) { update { copy(editing = value) } }

     data class ViewState (
         val trip: Trip? = null,
         val loading: Boolean = false,
         val error: String = "",
         val photos: List<Photo> = emptyList(),
         val editing: Boolean = false
     ) : State
}
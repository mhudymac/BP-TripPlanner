package kmp.android.gallery.viewmodel

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.ErrorResult
import kmp.shared.base.Result
import kmp.shared.domain.model.Photo
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.photos.GetPhotosByTripUseCase
import kmp.shared.domain.usecase.photos.RemovePhotoByUriUseCase
import kmp.shared.domain.usecase.photos.SavePhotoUseCase
import kmp.shared.domain.usecase.trip.DeleteTripUseCase
import kmp.shared.domain.usecase.trip.GetTripUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * This class represents the ViewModel for the Gallery view.
 * It provides functions to get a trip by its ID, delete a trip, delete a photo, get photos by trip ID,
 * get all information for a trip, add a user photo, and toggle editing mode.
 */
internal class GalleryViewModel(
    private val getTripUseCase: GetTripUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
    private val getPhotosByTripUseCase: GetPhotosByTripUseCase,
    private val savePhotoUseCase: SavePhotoUseCase,
    private val removePhotoByUriUseCase: RemovePhotoByUriUseCase
) : BaseStateViewModel<GalleryViewModel.ViewState>(ViewState()) {

    var currentPlaceId: String = ""

    private val _errorFlow = MutableSharedFlow<ErrorResult>(replay = 1)
    val errorFlow: Flow<ErrorResult> get() = _errorFlow

     private fun getTrip(tripId: Long) {
          launch {
              update { copy(loading = true) }
              getTripUseCase(tripId).collect {
                    when (it) {
                         is Result.Success -> { update { copy(trip = it.data) }}
                         is Result.Error -> { _errorFlow.emit(it.error) }
                    }
                    update { copy(loading = false) }
                }
          }
     }

    fun delete(){
        launch {
            lastState().trip?.let {
                deleteTripUseCase(it)
            }
        }
    }

    fun deletePhoto(photoUri: String){
        launch {
            removePhotoByUriUseCase(photoUri)
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
         val photos: List<Photo> = emptyList(),
         val editing: Boolean = false
     ) : State
}
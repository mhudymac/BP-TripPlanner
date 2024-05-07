package kmp.android.home.viewmodel

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.ErrorResult
import kmp.shared.domain.model.Photo
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.photos.SavePhotoUseCase
import kmp.shared.domain.usecase.trip.GetNearestTripUseCase
import kmp.shared.domain.usecase.trip.UpdateOnlyTripDetailsUseCase
import kmp.shared.base.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate

/**
 * This class represents the ViewModel for the Home view.
 * It provides functions to get the nearest trip, start a trip, finish a trip, add a user photo to a trip, and set the active trip.
 */
class HomeViewModel(
    private val getNearestTripUseCase: GetNearestTripUseCase,
    private val updateOnlyTripDetailsUseCase: UpdateOnlyTripDetailsUseCase,
    private val savePhotosUseCase: SavePhotoUseCase
) : BaseStateViewModel<HomeViewModel.ViewState>(ViewState()) {

    private val _errorFlow = MutableSharedFlow<ErrorResult>(replay = 1)
    val errorFlow: Flow<ErrorResult> get() = _errorFlow

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
                val result = updateOnlyTripDetailsUseCase(trip)
                if(result is Result.Error){
                    _errorFlow.emit(result.error)
                }
            }
        }
    }

    fun finishTrip() {
        val trip = lastState().trip?.copy(completed = true)
        if(trip != null) {
            launch {
                val result = updateOnlyTripDetailsUseCase(trip)
                if(result is Result.Error){
                    _errorFlow.emit(result.error)
                }
            }
        }
    }

    fun addUserPhoto(photoUri: String) {
        launch {
            val trip = lastState().trip
            if(trip != null) {
                val result = savePhotosUseCase(Photo(trip.activePlace, trip.id, photoUri))
                if(result is Result.Error){
                    _errorFlow.emit(result.error)
                }
            }
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
         val isActive: Boolean = false,
         val selectedTab: Int = 0
     ) : State
}
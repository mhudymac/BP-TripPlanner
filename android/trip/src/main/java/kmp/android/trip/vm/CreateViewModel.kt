package kmp.android.trip.vm

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.domain.model.Place
import java.time.LocalDateTime

class CreateViewModel(

) : BaseStateViewModel<CreateViewModel.ViewState>(ViewState()) {

    fun addPlace(place: Place) {
        update { copy(itinerary = itinerary + place) }
    }

    fun updateName(name: String) {
        update { copy(name = name) }
    }

    fun updateDate(date: LocalDateTime) {
        update { copy(date = date) }
    }

    data class ViewState(
        val name: String = "",
        val date: LocalDateTime? = null,
        val start: Place? = null,
        val itinerary: List<Place> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : State

}
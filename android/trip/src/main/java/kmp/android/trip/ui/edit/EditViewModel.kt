package kmp.android.trip.ui.edit

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.Result
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import kmp.shared.domain.usecase.trip.GetTripUseCase
import kmp.shared.domain.usecase.trip.RemoveTripUseCase
import kmp.shared.domain.usecase.trip.SaveTripUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate

class EditViewModel(
    private val getTripByName: GetTripUseCase,
    private val saveTripUseCase: SaveTripUseCase,
    private val removeTrip: RemoveTripUseCase
) : BaseStateViewModel<EditViewModel.ViewState>(ViewState()) {

    private var id: Long = 0

    fun getTrip(tripId: String) {
        launch {
            update { copy(loading = true) }
            getTripByName(tripId).map {
                when (it) {
                    is Result.Success -> { update { copy(
                            name = it.data.name,
                            date = it.data.date.toJavaLocalDate(),
                            itinerary = it.data.itinerary.drop(1),
                            start = it.data.itinerary.firstOrNull()
                        ) }
                        id = it.data.id
                    }
                    is Result.Error -> { update { copy(error = it.error.message?: "") }}
                }
            }.collect()

            update { copy(loading = false) }
        }
    }

    fun saveTrip() {
        launch {
            if( lastState().name.isEmpty() )
                update { copy(error = "Name is required") }
            else if( lastState().date == null )
                update { copy(error = "Date is required") }
            else if( lastState().start == null )
                update { copy(error = "Start place is required") }
            else if( lastState().itinerary.isEmpty() )
                update { copy(error = "At least one place is required") }
            else{
                val trip = lastState().let { state ->
                    Trip(
                        id = id,
                        name = state.name,
                        date = state.date!!.toKotlinLocalDate(),
                        itinerary = listOf(state.start!!) + state.itinerary,
                        order = (listOf(state.start) + state.itinerary).map { it.id },
                    )
                }

                saveTripUseCase(trip)
                update{ copy(saveSuccess = true) }
            }
        }
    }

    fun updateTripOrder(list: List<Place>) {
        update { copy(itinerary = list) }
    }

    fun onNameChange(name: String) {
        update { copy(name = name) }
    }

    fun onDateSelected(date: LocalDate) {
        update { copy(date = date) }
    }

    fun onAddPlace(place: Place) {
        update { copy(itinerary = itinerary.plus(place)) }
    }

    fun onRemovePlace(place: Place) {
        update { copy(itinerary = itinerary.minus(place)) }
    }

    fun toggleReordering() {
        update { copy(reordering = !reordering) }
    }

    data class ViewState (
        val name: String = "",
        val date: LocalDate? = null,
        val start: Place? = null,
        val itinerary: List<Place> = emptyList(),
        val loading: Boolean = false,
        val error: String = "",
        val reordering: Boolean = false,
        val saveSuccess: Boolean = false
    ) : State
}

//package kmp.android.trip.vm
//
//import kmp.android.shared.core.system.BaseStateViewModel
//import kmp.android.shared.core.system.State
//import kmp.shared.domain.model.Trip
//import kmp.shared.domain.usecase.trip.GetAllTripsUseCase
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.emptyFlow
//
//class HomeViewModel(
//    private val getAllTripsUseCase: GetAllTripsUseCase
//) : BaseStateViewModel<HomeViewModel.ViewState>(ViewState()) {
//
//
//
//    fun loadTrips() {
//        launch {
//            update { copy(isLoading = true) }
//            val trips = getAllTripsUseCase()
//            update { copy(trips = trips, isLoading = false) }
//        }
//    }
//
//
//}
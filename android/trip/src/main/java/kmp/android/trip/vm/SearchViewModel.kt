package kmp.android.trip.vm

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.domain.model.Place
import kmp.shared.domain.usecase.place.SearchPlacesUseCase
import kmp.shared.base.Result
import kmp.shared.domain.usecase.place.UpdatePhotoUrlUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class SearchViewModel(
    private val searchPlaces: SearchPlacesUseCase,
    private val updatePhotoUrl: UpdatePhotoUrlUseCase
) : BaseStateViewModel<SearchViewModel.ViewState>(ViewState()){

    fun search(query: String) {
        launch {
            loading = true
            when (val res = searchPlaces(query)) {
                is Result.Success -> {
                    val photoResults = res.data.map { place ->
                        async { updatePhotoUrl(place) }
                    }.awaitAll()

                    update {
                        copy(places = photoResults.mapNotNull {
                            when (it) {
                                is Result.Success -> it.data
                                is Result.Error -> it.data
                            }
                        })
                    }
                }
                is Result.Error -> update { copy(error = res.error.message) }
            }
            loading = false
        }
    }

    fun clear(){
        update { copy(
            places = emptyList(),
            searchedQuery = ""
        )}
    }

    fun changeQuery(query: String) {
        update { copy(searchedQuery = query) }
    }

    private var loading
        get() = lastState().isLoading
        set(value) =  update { copy(isLoading = value) }

    data class ViewState(
        val searchedQuery: String = "",
        val places: List<Place> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : State
}
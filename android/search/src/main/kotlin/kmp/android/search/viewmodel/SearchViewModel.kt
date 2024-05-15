package kmp.android.search.viewmodel

import kmp.android.shared.core.system.BaseStateViewModel
import kmp.android.shared.core.system.State
import kmp.shared.base.ErrorResult
import kmp.shared.domain.model.Place
import kmp.shared.domain.usecase.place.SearchPlacesUseCase
import kmp.shared.domain.model.Location
import kmp.shared.domain.usecase.place.SearchPlacesWithBiasUseCase
import kmp.shared.domain.usecase.place.UpdatePhotoUrlUseCase
import kotlinx.coroutines.async
import kmp.shared.base.Result
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * This class represents the ViewModel for the Search view.
 * It provides functions to search for places, clear the search results, toggle the search status, and change the search query.
 */
class SearchViewModel(
    private val searchPlaces: SearchPlacesUseCase,
    private val searchPlacesWithBias: SearchPlacesWithBiasUseCase,
    private val updatePhotoUrl: UpdatePhotoUrlUseCase
) : BaseStateViewModel<SearchViewModel.ViewState>(ViewState()){

    private val _errorFlow = MutableSharedFlow<ErrorResult>(replay = 1)
    val errorFlow: Flow<ErrorResult> get() = _errorFlow

    var location: Location? = null
    fun search(query: String) {
        if(query.isEmpty()) return

        launch {
            loading = true
            when (val res = location?.let { searchPlacesWithBias(SearchPlacesWithBiasUseCase.Params(query, it)) }?: searchPlaces(SearchPlacesUseCase.Params(query))) {
                is Result.Success -> {
                    val places = res.data.map { place ->
                        async {
                            when(val placeWithPhoto = updatePhotoUrl(place)){
                                is Result.Success -> placeWithPhoto.data
                                is Result.Error -> place
                            }
                        }
                    }.awaitAll()

                    update { copy(places = places) }
                }
                is Result.Error -> _errorFlow.emit(res.error)
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

    fun toggleSearch(bool: Boolean){
        update { copy(isSearching = bool) }
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
        val isSearching: Boolean = true,
    ) : State
}
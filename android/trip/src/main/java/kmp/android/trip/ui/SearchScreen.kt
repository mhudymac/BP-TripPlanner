package kmp.android.trip.ui


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import kmp.android.shared.core.util.get
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.vm.SearchViewModel
import org.koin.androidx.compose.getViewModel
import kmp.android.trip.vm.SearchViewModel.ViewState as State

fun NavController.navigateToSearchScreen() {
    navigate(TripGraph.Search())
}

internal fun NavGraphBuilder.searchScreenRoute() {
    composableDestination(
        destination = TripGraph.Search
    ) {
        SearchScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(
    viewModel: SearchViewModel = getViewModel()
) {
    val places by viewModel[State::places].collectAsState(emptyList())
    val loading by viewModel[State::isLoading].collectAsState(false)
    val searchedQuery by viewModel[State::searchedQuery].collectAsState("")



    SearchBar(
        query = searchedQuery,
        onQueryChange = {newQuery -> viewModel.changeQuery(newQuery)},
        onSearch = {searchQuery -> viewModel.search(searchQuery)},
        active = true,
        onActiveChange = {}
    ) {
        LazyColumn {
            items(places) { place ->
                Text(place.name)
            }
        }
    }
}

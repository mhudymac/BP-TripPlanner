package kmp.android.trip.ui.search


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import coil.compose.AsyncImage
import kmp.android.shared.core.util.get
import kmp.android.shared.navigation.bottomSheetDestination
import kmp.android.shared.navigation.dialogDestination
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.ui.create.PlaceCard
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Place
import org.koin.androidx.compose.getViewModel
import kmp.android.trip.ui.search.SearchViewModel.ViewState as State

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchScreen(
    viewModel: SearchViewModel = getViewModel(),
    onPlaceSelected: (Place) -> Unit = {},
    location: Location? = null
) {
    val places by viewModel[State::places].collectAsState(emptyList())
    val loading by viewModel[State::isLoading].collectAsState(false)
    val searchedQuery by viewModel[State::searchedQuery].collectAsState("")
    val isSearching by viewModel[State::isSearching].collectAsState(true)

    LaunchedEffect(Unit){
        location?.let { viewModel.location = it}
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        SearchBar(
            query = searchedQuery,
            onQueryChange = { newQuery -> viewModel.changeQuery(newQuery) },
            onSearch = { searchQuery -> viewModel.search(searchQuery); viewModel.toggleSearch(false) },
            active = isSearching,
            onActiveChange = { viewModel.toggleSearch(it) },
            trailingIcon = {
                if (searchedQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.clear() }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear")
                    }
                }
            },
            placeholder = { Text("Search for a place") },
            modifier = Modifier.fillMaxWidth(),
            content = {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                ) {
                    items(places) { place ->
                        PlaceCard(
                            place = place,
                            onClick = { viewModel.clear(); onPlaceSelected(place) }
                        )
                    }
                }
            }
        )
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) {
                items(places) { place ->
                    PlaceCard(
                        place = place,
                        onClick = { viewModel.clear(); onPlaceSelected(place) }
                    )
                }
            }
        }
    }
}

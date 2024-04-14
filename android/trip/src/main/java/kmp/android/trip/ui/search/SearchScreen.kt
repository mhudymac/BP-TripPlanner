package kmp.android.trip.ui.search


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kmp.android.shared.core.util.get
import kmp.android.trip.ui.components.FullScreenLoading
import kmp.android.trip.ui.components.PlaceCard
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Place
import org.koin.androidx.compose.getViewModel
import kmp.android.shared.R
import kmp.android.trip.ui.search.SearchViewModel.ViewState as State

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBarSearch(
    onDismissRequest: () -> Unit,
    onPlaceSelected: (Place) -> Unit,
    location: Location? = null
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        SearchScreen(
            onPlaceSelected = onPlaceSelected,
            location = location,
        )
    }
}

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
                        Icon(Icons.Filled.Clear, contentDescription = stringResource(id = R.string.clear))
                    }
                }
            },
            placeholder = { Text(stringResource(id = R.string.search_place)) },
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
            },
        )
        if (loading) {
            FullScreenLoading(stringResource(id = R.string.place_search_loading))
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

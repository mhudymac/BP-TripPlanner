package kmp.android.trip.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import coil.compose.AsyncImage
import kmp.android.shared.core.util.get
import kmp.android.shared.navigation.dialogDestination
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.vm.SearchViewModel
import kmp.shared.domain.model.Place
import org.koin.androidx.compose.getViewModel
import kmp.android.trip.vm.SearchViewModel.ViewState as State

fun NavController.navigateToSearchScreen() {
    navigate(TripGraph.Search())
}

internal fun NavGraphBuilder.searchScreenRoute() {
    dialogDestination(
        destination = TripGraph.Search
    ) {
        SearchScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchScreen(
    viewModel: SearchViewModel = getViewModel(),
    onPlaceSelected: (Place) -> Unit = {}
) {
    val places by viewModel[State::places].collectAsState(emptyList())
    val loading by viewModel[State::isLoading].collectAsState(false)
    val searchedQuery by viewModel[State::searchedQuery].collectAsState("")



    SearchBar(
        query = searchedQuery,
        onQueryChange = {newQuery -> viewModel.changeQuery(newQuery)},
        onSearch = {searchQuery -> viewModel.search(searchQuery)},
        active = true,
        onActiveChange = {},
        trailingIcon = {
            if(searchedQuery.isNotEmpty()) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "Clear",
                    modifier = Modifier.clickable(
                        onClick = { viewModel.changeQuery("") }
                    )
                )
            }
        },
        placeholder = { Text("Search") },
    ) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(places) { place ->
                ImageAndText(onclick = { onPlaceSelected(place); viewModel.clear() }, text = place.name, imageUrl = place.photoUri)
            }
        }
    }
}

@Composable
internal fun ImageAndText(onclick: () -> Unit, text: String, imageUrl: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onclick() }
    ){
        AsyncImage(
            model = imageUrl,
            contentDescription = null
        )
        Text(text = text)
    }
}

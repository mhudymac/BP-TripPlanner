package kmp.android.trip.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraphBuilder
import kmp.android.shared.core.util.get
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.ui.components.lists.TripListWithButtons
import org.koin.androidx.compose.getViewModel
import kmp.android.shared.R
import kmp.android.trip.ui.list.ListViewModel.ViewState as State

internal fun NavGraphBuilder.tripListRoute(
    navigateToCreateScreen: () -> Unit,
    navigateToDetailScreen: (Long) -> Unit,
    navigateToGalleryScreen: (Long) -> Unit,
    navigateToEditScreen: (Long) -> Unit,
    navigateToHomeScreen: () -> Unit
) {
    composableDestination(
        destination = TripGraph.List
    ) {
        TripListScreenRoute(
            navigateToCreateScreen,
            navigateToDetailScreen,
            navigateToGalleryScreen,
            navigateToEditScreen,
            navigateToHomeScreen
        )
    }
}

@Composable
internal fun TripListScreenRoute(
    navigateToCreateScreen: () -> Unit,
    navigateToDetailScreen: (Long) -> Unit,
    navigateToGalleryScreen: (Long) -> Unit,
    navigateToEditScreen: (Long) -> Unit,
    navigateToHomeScreen: () -> Unit,
    viewModel: ListViewModel = getViewModel()
) {
    val loadingUpcoming by viewModel[State::loadingUpcoming].collectAsState(false)
    val loadingCompleted by viewModel[State::loadingCompleted].collectAsState(false)
    val unCompletedTrips by viewModel[State::uncompletedTrips].collectAsState(emptyList())
    val completedTrips by viewModel[State::completedTrips].collectAsState(emptyList())
    val selectedTab by viewModel[State::selectedTab].collectAsState(0)
    val editId by viewModel[State::editId].collectAsState(null)
    val error by viewModel[State::error].collectAsState("")

    val scrollState = rememberLazyListState()
    val isFloatingButtonExpanded = remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset <= 0 } }

    LaunchedEffect(editId){
        editId?.let { viewModel.clearEdit();  navigateToEditScreen(it) }
    }

    val snackHost = remember { SnackbarHostState() }

    LaunchedEffect(error){
        if(error.isNotEmpty()){
            snackHost.showSnackbar(error)
        }
    }

    Scaffold(
        floatingActionButton = {
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(id = R.string.create)) },
                    icon = { Icon(Icons.Filled.Add, "") },
                    onClick = { navigateToCreateScreen() },
                    expanded = isFloatingButtonExpanded.value,
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackHost) },
    ) {

        Column(Modifier.padding(it)) {
            TabRow(
                selectedTabIndex = selectedTab,
                indicator = {},
                divider = {},
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { viewModel.selectedTab = 0 },
                    text = {
                        TabText(stringResource(id = R.string.tab_1_text), selectedTab == 0)
                    },
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { viewModel.selectedTab = 1 },
                    text = {
                        TabText(stringResource(id = R.string.tab_2_text), selectedTab == 1)
                    },
                )
            }

            when (selectedTab) {
                0 -> TripListWithButtons(
                    trips = unCompletedTrips,
                    onTripClick = navigateToDetailScreen,
                    onTripButtonClick = { trip -> viewModel.startTrip(trip); navigateToHomeScreen() },
                    loading = loadingUpcoming,
                    scrollState = scrollState,
                ) {
                    Icon(Icons.Default.PlayCircle, stringResource(id = R.string.start_trip))
                    Text(stringResource(id = R.string.start_trip), style = MaterialTheme.typography.labelMedium)
                }
                1 -> TripListWithButtons(
                    trips = completedTrips,
                    onTripClick = navigateToGalleryScreen,
                    onTripButtonClick = { trip -> viewModel.repeatTrip(trip) },
                    loading = loadingCompleted,
                    scrollState = scrollState
                ) {
                    Icon(Icons.Default.Repeat, stringResource(id = R.string.trip_repeat))
                    Text(stringResource(id = R.string.trip_repeat), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun TabText(text: String, selected: Boolean) {
    Text(
        text = text,
        style = if (selected) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyMedium,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.ExtraLight
    )
}




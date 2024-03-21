package kmp.android.trip.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import kmp.android.shared.core.util.get
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.vm.ListViewModel
import kmp.shared.domain.model.Trip
import org.koin.androidx.compose.getViewModel
import kmp.android.trip.vm.ListViewModel.ViewState as State

internal fun NavGraphBuilder.tripListRoute(
    navigateToCreateScreen: () -> Unit
) {
    composableDestination(
        destination = TripGraph.List
    ) {
        TripListScreenRoute(navigateToCreateScreen)
    }
}

@Composable
internal fun TripListScreenRoute(navigateToCreateScreen: () -> Unit) {
    TripListScreen(navigateToCreateScreen)
}

@Composable
private fun TripListScreen(
    navigateToCreateScreen: () -> Unit,
    viewModel: ListViewModel = getViewModel()
) {
    val loading by viewModel[State::isLoading].collectAsState(false)
    val trips by viewModel[State::trips].collectAsState(emptyList())

    var selectedTab by remember { mutableIntStateOf(0) }

    Box(Modifier.fillMaxSize()) {
        Column {

            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.statusBarsPadding()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("New Trips") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Completed Trips") }
                )
            }

            when (selectedTab) {
                0 -> NewTripsScreen( trips )
                1 -> FullScreenText("Completed Trips")
            }
        }

        if(selectedTab == 0) {
            ExtendedFloatingActionButton(
                text = { Text("Create") },
                icon = { Icon(Icons.Filled.Add, "") },
                onClick = { navigateToCreateScreen() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                expanded = true
            )
        }
    }
}

@Composable
private fun NewTripsScreen( trips: List<Trip> ) {
        LazyColumn {
            items(trips) { trip ->
                Text(trip.name)
            }
        }

}


@Composable
internal fun FullScreenText( text: String ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text)
    }
}
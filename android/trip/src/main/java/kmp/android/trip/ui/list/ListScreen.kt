package kmp.android.trip.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import kmp.android.shared.core.util.get
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph
import kmp.shared.domain.model.Trip
import kotlinx.datetime.toJavaLocalDate
import org.koin.androidx.compose.getViewModel
import java.time.format.DateTimeFormatter
import kmp.android.trip.ui.list.ListViewModel.ViewState as State

internal fun NavGraphBuilder.tripListRoute(
    navigateToCreateScreen: () -> Unit,
    navigateToDetailScreen: (Long) -> Unit
) {
    composableDestination(
        destination = TripGraph.List
    ) {
        TripListScreenRoute(
            navigateToCreateScreen,
            navigateToDetailScreen
        )
    }
}

@Composable
internal fun TripListScreenRoute(
    navigateToCreateScreen: () -> Unit,
    navigateToDetailScreen: (Long) -> Unit
) {
    TripListScreen(
        navigateToCreateScreen,
        navigateToDetailScreen
    )
}

@Composable
private fun TripListScreen(
    navigateToCreateScreen: () -> Unit,
    navigateToDetailScreen: (Long) -> Unit,
    viewModel: ListViewModel = getViewModel()
) {
    val loading by viewModel[State::isLoading].collectAsState(false)
    val trips by viewModel[State::trips].collectAsState(emptyList())

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        floatingActionButton = {
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(
                    text = { Text("Create") },
                    icon = { Icon(Icons.Filled.Add, "") },
                    onClick = { navigateToCreateScreen() },
                    expanded = true
                )
            }
        }
    ) {
        Column(Modifier.padding(it)) {
            TabRow(
                selectedTabIndex = selectedTab,
                indicator = {},
                divider = {},
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(
                        "New Trips",
                        style = if (selectedTab == 0) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.ExtraLight
                    )},
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(
                        "Completed Trips",
                        style = if (selectedTab == 1) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.ExtraLight
                    )}
                )
            }

            when (selectedTab) {
                0 -> NewTripsScreen( trips, navigateToDetailScreen )
                1 -> FullScreenText("Completed Trips")
            }
        }
    }
}

@Composable
private fun NewTripsScreen(trips: List<Trip>, navigateToDetailScreen: (Long) -> Unit) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        items(trips) { trip ->
            TripCard(trip = trip, onClick = { navigateToDetailScreen(trip.id) })
        }
    }
}

@Composable
internal fun TripCard(
    trip: Trip,
    onClick: () -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.large,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = trip.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = trip.date.toJavaLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    style = MaterialTheme.typography.titleSmall
                )
            }
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
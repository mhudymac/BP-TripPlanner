package kmp.android.trip.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    navigateToDetailScreen: (Long) -> Unit,
    navigateToGalleryScreen: (Long) -> Unit,
    navigateToEditScreen: (Long) -> Unit
) {
    composableDestination(
        destination = TripGraph.List
    ) {
        TripListScreenRoute(
            navigateToCreateScreen,
            navigateToDetailScreen,
            navigateToGalleryScreen,
            navigateToEditScreen
        )
    }
}

@Composable
internal fun TripListScreenRoute(
    navigateToCreateScreen: () -> Unit,
    navigateToDetailScreen: (Long) -> Unit,
    navigateToGalleryScreen: (Long) -> Unit,
    navigateToEditScreen: (Long) -> Unit,
    viewModel: ListViewModel = getViewModel()
) {
    val loadingUpcoming by viewModel[State::loadingUpcoming].collectAsState(false)
    val loadingCompleted by viewModel[State::loadingCompleted].collectAsState(false)
    val unCompletedTrips by viewModel[State::uncompletedTrips].collectAsState(emptyList())
    val completedTrips by viewModel[State::completedTrips].collectAsState(emptyList())
    val selectedTab by viewModel[State::selectedTab].collectAsState(0)
    val editId by viewModel[State::editId].collectAsState(null)

    LaunchedEffect(editId){
        editId?.let { viewModel.clearEdit();  navigateToEditScreen(it) }

    }

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
                    onClick = { viewModel.selectedTab = 0 },
                    text = {
                        Text(
                            "Upcoming Trips",
                            style = if (selectedTab == 0) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.ExtraLight
                        )
                    },
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { viewModel.selectedTab = 1 },
                    text = {
                        Text(
                            "Completed Trips",
                            style = if (selectedTab == 1) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.ExtraLight
                        )
                    }
                )
            }

            when (selectedTab) {
                0 -> TripList(
                    unCompletedTrips,
                    navigateToDetailScreen,
                    loadingUpcoming
                ) {
                    Button(onClick = { viewModel.startTrip(it) }) {
                        Icon(Icons.Default.PlayCircle, "Start Icon")
                        Text("Start trip")
                    }
                }
                1 -> TripList(
                    completedTrips,
                    navigateToGalleryScreen,
                    loadingCompleted,
                ) {
                    Button(onClick = { viewModel.repeatTrip(it) }) {
                        Icon(Icons.Default.Repeat, "Repeat Icon")
                        Text("Repeat trip")
                    }
                }
            }
        }
    }
}

@Composable
private fun TripList(trips: List<Trip>, onTripClick: (Long) -> Unit, loading: Boolean, content: @Composable (Trip) -> Unit = {}) {
    if(loading){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(trips) { trip ->
                TripCard(trip = trip, onClick = { onTripClick(trip.id) }) {
                    content(trip)
                }
            }
        }
    }
}

@Composable
internal fun TripCard(
    trip: Trip,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {}
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
                content()
            }
        }
    }
}
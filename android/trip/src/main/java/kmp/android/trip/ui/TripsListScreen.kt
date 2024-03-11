package kmp.android.trip.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph

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
private fun TripListScreen(navigateToCreateScreen: () -> Unit) {
    val selectedTab = remember { mutableIntStateOf(0) }

    TabRow(selectedTabIndex = selectedTab.intValue, modifier = Modifier.statusBarsPadding()) {
        Tab(
            selected = selectedTab.intValue == 0,
            onClick = { selectedTab.intValue = 0 },
            text = { Text("New Trips") }
        )
        Tab(
            selected = selectedTab.intValue == 1,
            onClick = { selectedTab.intValue = 1 },
            text = { Text("Completed Trips") }
        )
    }

    when (selectedTab.intValue) {
        0 -> NewTripsScreen(navigateToCreateScreen)
        1 -> FullScreenText("Completed Trips")
    }
}

@Composable
private fun NewTripsScreen(navigateToCreateScreen: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        FullScreenText("New Trips")

        ExtendedFloatingActionButton(
            text = { Text("Create") },
            icon = { Icon(Icons.Filled.Add, "") },
            onClick = { navigateToCreateScreen() },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }
}


@Composable
internal fun FullScreenText(text: String) {
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
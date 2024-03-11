package kmp.android.trip.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph


internal fun NavGraphBuilder.tripHomeRoute() {
    composableDestination(
        destination = TripGraph.Home
    ) {
        HomeScreenRoute()
    }
}

@Composable
internal fun HomeScreenRoute(){
    HomeScreen()
}

@Composable
private fun HomeScreen(){
    FullScreenText(text = "Home")
}
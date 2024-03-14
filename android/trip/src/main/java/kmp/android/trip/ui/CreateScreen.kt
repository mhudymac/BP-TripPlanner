package kmp.android.trip.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph

fun NavController.navigateToCreateScreen() {
    navigate(TripGraph.Create())
}

internal fun NavGraphBuilder.createScreenRoute(navigateToSearchScreen: () -> Unit) {
    composableDestination(
        destination = TripGraph.Create,
    ) {
        CreateScreen(navigateToSearchScreen)
    }
}

@Composable
private fun CreateScreen(navigateToSearchScreen: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create")
        Button(onClick = { navigateToSearchScreen() }) {
            Icon(Icons.Filled.AccountBox, "")
        }
    }

}
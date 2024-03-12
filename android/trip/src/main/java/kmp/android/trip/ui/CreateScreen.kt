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
import com.google.accompanist.navigation.material.bottomSheet
import kmp.android.shared.navigation.bottomSheetDestination
import kmp.android.shared.navigation.composableDestination
import kmp.android.shared.navigation.dialogDestination
import kmp.android.trip.navigation.TripGraph

fun NavController.navigateToCreateScreen() {
    navigate(TripGraph.Create())
}

internal fun NavGraphBuilder.createScreenRoute() {
    composableDestination(
        destination = TripGraph.Create,
    ) {
        CreateScreen()
    }
}

@Composable
private fun CreateScreen() {
    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create")
        Button(onClick = { /*TODO*/ }) {
            Icon(Icons.Filled.AccountBox, "")
        }
    }

}
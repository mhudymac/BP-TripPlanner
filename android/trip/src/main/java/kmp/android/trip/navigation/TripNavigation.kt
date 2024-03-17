package kmp.android.trip.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import kmp.android.trip.ui.createScreenRoute
import kmp.android.trip.ui.navigateToCreateScreen
import kmp.android.trip.ui.navigateToSearchScreen
import kmp.android.trip.ui.searchScreenRoute
import kmp.android.trip.ui.tripHomeRoute
import kmp.android.trip.ui.tripListRoute

fun NavGraphBuilder.tripNavGraph(
    navHostController: NavHostController
) {
    navigation(
        startDestination = TripGraph.Home.route,
        route = TripGraph.rootPath
    ) {
        tripHomeRoute()
        tripListRoute { navHostController.navigateToCreateScreen() }
        createScreenRoute{ navHostController.navigateToSearchScreen() }
        searchScreenRoute()
    }
}
package kmp.android.trip.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import kmp.android.trip.ui.create.createScreenRoute
import kmp.android.trip.ui.detail.detailScreenRoute
import kmp.android.trip.ui.create.navigateToCreateScreen
import kmp.android.trip.ui.detail.navigateToDetailScreen
import kmp.android.trip.ui.home.tripHomeRoute
import kmp.android.trip.ui.list.tripListRoute

fun NavGraphBuilder.tripNavGraph(
    navHostController: NavHostController
) {
    navigation(
        startDestination = TripGraph.Home.route,
        route = TripGraph.rootPath
    ) {
        tripHomeRoute()
        tripListRoute(
            navigateToCreateScreen = { navHostController.navigateToCreateScreen() },
            navigateToDetailScreen = { tripId -> navHostController.navigateToDetailScreen(tripId) }
        )
        createScreenRoute { navHostController.navigateUp() }
        //searchScreenRoute()
        detailScreenRoute { navHostController.navigateUp() }
    }
}
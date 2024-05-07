package kmp.android.trip.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import kmp.android.trip.screens.detail.detailScreenRoute
import kmp.android.trip.screens.detail.navigateToDetailScreen
import kmp.android.trip.screens.edit.editScreenRoute
import kmp.android.trip.screens.edit.navigateToCreateScreen
import kmp.android.trip.screens.edit.navigateToEditScreen
import kmp.android.trip.screens.list.tripListRoute

fun NavGraphBuilder.tripNavGraph(
    navHostController: NavHostController,
    navigateToGallery: (Long) -> Unit,
    navigateToHomeScreen: () -> Unit
) {
    navigation(
        startDestination = TripGraph.List.route,
        route = TripGraph.rootPath
    ) {
        tripListRoute(
            navigateToCreateScreen = { navHostController.navigateToCreateScreen() },
            navigateToDetailScreen = { tripId -> navHostController.navigateToDetailScreen(tripId) },
            navigateToGalleryScreen = { tripId -> navigateToGallery(tripId) },
            navigateToEditScreen = { tripId -> navHostController.navigateToEditScreen(tripId) },
            navigateToHomeScreen = { navigateToHomeScreen() }
        )
        detailScreenRoute(
            navigateUp = { navHostController.navigateUp() },
            navigateToEdit = { tripId ->
                navHostController.navigateToEditScreen(
                    tripId = tripId,
                )
            }
        )
        editScreenRoute( navigateUp = { navHostController.navigateUp() })
    }
}
package kmp.android.trip.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import kmp.android.trip.ui.create.createScreenRoute
import kmp.android.trip.ui.create.navigateToCreateScreen
import kmp.android.trip.ui.detail.detailScreenRoute
import kmp.android.trip.ui.detail.navigateToDetailScreen
import kmp.android.trip.ui.edit.editScreenRoute
import kmp.android.trip.ui.edit.navigateToEditScreen
import kmp.android.trip.ui.gallery.galleryScreenRoute
import kmp.android.trip.ui.gallery.navigateToGalleryScreen
import kmp.android.trip.ui.home.tripHomeRoute
import kmp.android.trip.ui.list.tripListRoute

fun NavGraphBuilder.tripNavGraph(
    navHostController: NavHostController
) {
    navigation(
        startDestination = TripGraph.Home.route,
        route = TripGraph.rootPath
    ) {
        tripHomeRoute( navigateToCreateScreen = { navHostController.navigateToCreateScreen() })
        tripListRoute(
            navigateToCreateScreen = { navHostController.navigateToCreateScreen() },
            navigateToDetailScreen = { tripId -> navHostController.navigateToDetailScreen(tripId) },
            navigateToGalleryScreen = { tripId -> navHostController.navigateToGalleryScreen(tripId) },
            navigateToEditScreen = { tripId -> navHostController.navigateToEditScreen( tripId)}
        )
        createScreenRoute { navHostController.navigateUp() }
        detailScreenRoute(
            navigateUp = { navHostController.navigateUp() },
            navigateToEdit = { tripId ->
                navHostController.navigateToEditScreen(
                    tripId = tripId,
                )
            }
        )
        galleryScreenRoute { navHostController.navigateUp() }
        editScreenRoute(navigateUp = { navHostController.navigateUp() })
    }
}
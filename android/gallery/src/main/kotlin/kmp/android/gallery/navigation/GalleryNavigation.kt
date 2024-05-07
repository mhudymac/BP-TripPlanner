package kmp.android.gallery.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import kmp.android.gallery.ui.galleryScreenRoute

fun NavGraphBuilder.galleryNavGraph(
    navHostController: NavHostController,
) {
    galleryScreenRoute( navigateUp = { navHostController.navigateUp() })
}
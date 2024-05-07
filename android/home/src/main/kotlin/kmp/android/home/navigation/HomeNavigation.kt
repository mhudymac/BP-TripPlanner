package kmp.android.home.navigation

import androidx.navigation.NavGraphBuilder
import kmp.android.home.ui.homeRoute

fun NavGraphBuilder.homeNavGraph(
    navigateToCreateScreen: () -> Unit,
) {
    homeRoute( navigateToCreateScreen )
}
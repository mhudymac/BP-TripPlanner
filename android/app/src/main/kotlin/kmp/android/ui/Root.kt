package kmp.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kmp.android.gallery.navigation.galleryNavGraph
import kmp.android.gallery.ui.navigateToGalleryScreen
import kmp.android.home.navigation.HomeDestination
import kmp.android.home.navigation.homeNavGraph
import kmp.android.home.ui.navigateToHomeScreen
import kmp.android.navigation.NavBarFeature
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.navigation.tripNavGraph
import kmp.android.trip.screens.edit.navigateToCreateScreen

@Composable
fun Root(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    Scaffold(
        modifier = modifier,
        bottomBar = { BottomBar(navController) },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            NavHost(
                navController,
                startDestination = HomeDestination.route
            ) {
                tripNavGraph(
                    navHostController = navController,
                    navigateToGallery = { id -> navController.navigateToGalleryScreen(id) },
                    navigateToHomeScreen = { navController.navigateToHomeScreen() }
                )
                galleryNavGraph( navHostController = navController )
                homeNavGraph { navController.navigateToCreateScreen() }
            }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if(currentRoute?.equals(HomeDestination.route) == true || currentRoute?.equals(TripGraph.List.route) == true) {
        NavigationBar(
            modifier = Modifier.navigationBarsPadding(),
        ) {
            NavBarFeature.entries.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        when (screen) {
                            NavBarFeature.Home -> Icon(Icons.Filled.Home, NavBarFeature.Home.name)
                            NavBarFeature.TripsList -> Icon(Icons.AutoMirrored.Filled.List, NavBarFeature.TripsList.name)
                        }
                    },
                    label = { Text(stringResource(screen.titleRes)) },
                    selected = (currentRoute == screen.route),
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}

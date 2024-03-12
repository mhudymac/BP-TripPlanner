package kmp.android.ui

//import kmp.android.books.navigation.booksNavGraph
//import kmp.android.login.navigation.LoginDestination
//import kmp.android.login.navigation.loginNavGraph
//import kmp.android.profile.navigation.profileNavGraph
//import kmp.android.recipes.navigation.recipesNavGraph
//import kmp.android.users.navigation.UsersGraph
//import kmp.android.users.navigation.usersNavGraph
import androidx.compose.foundation.layout.Box
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
import kmp.android.navigation.NavBarFeature
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.navigation.tripNavGraph

@Composable
fun Root(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier,
        bottomBar = { BottomBar(navController) },
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
                NavHost(
                    navController,
                    startDestination = TripGraph.rootPath // if (showLogin) LoginDestination.route else UsersGraph.rootPath
                ) {
                    tripNavGraph(navController)
                }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if(currentRoute?.equals(TripGraph.Home.route) == true || currentRoute?.equals(TripGraph.List.route) == true) {
        NavigationBar(
            modifier = Modifier.navigationBarsPadding(),
        ) {
            NavBarFeature.entries.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        when (screen) {
                            NavBarFeature.Home -> Icon(Icons.Filled.Home, "")
                            NavBarFeature.TripsList -> Icon(Icons.AutoMirrored.Filled.List, "")
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

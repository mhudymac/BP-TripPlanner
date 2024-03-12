package kmp.android.navigation

import androidx.annotation.StringRes
//import kmp.android.books.navigation.BooksGraph
//import kmp.android.profile.navigation.ProfileGraph
//import kmp.android.recipes.navigation.RecipesGraph
import kmp.android.shared.R
import kmp.android.trip.navigation.TripGraph
//import kmp.android.users.navigation.UsersGraph

enum class NavBarFeature(val route: String, @StringRes val titleRes: Int) {
    Home(TripGraph.Home.route, R.string.bottom_bar_home),
    TripsList(TripGraph.List.route, R.string.bottom_bar_list)
}
 
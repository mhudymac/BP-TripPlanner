package kmp.android.navigation

import androidx.annotation.StringRes
import kmp.android.home.navigation.HomeDestination
import kmp.android.shared.R
import kmp.android.trip.navigation.TripGraph

enum class NavBarFeature(val route: String, @StringRes val titleRes: Int) {
    Home(HomeDestination.route, R.string.bottom_bar_item_1),
    TripsList(TripGraph.List.route, R.string.bottom_bar_item_2)
}
 
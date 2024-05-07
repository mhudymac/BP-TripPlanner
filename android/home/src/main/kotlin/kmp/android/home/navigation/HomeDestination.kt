package kmp.android.home.navigation

import kmp.android.shared.navigation.Destination

data object HomeDestination : Destination(parent = null) {
    override val routeDefinition: String = "home"
}
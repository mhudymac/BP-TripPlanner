package kmp.android.trip.navigation

import kmp.android.shared.navigation.Destination
import kmp.android.shared.navigation.FeatureGraph

object TripGraph : FeatureGraph(parent = null) {
    override val path: String = "trips"

    object Home : Destination(parent = this) {
        override val routeDefinition: String = "home"
    }
    object List : Destination(parent = this) {
        override val routeDefinition: String = "list"
    }
    object Create : Destination(parent = this) {
        override val routeDefinition: String = "create"
    }
    object Search : Destination(parent = this) {
        override val routeDefinition: String = "search"
    }
}
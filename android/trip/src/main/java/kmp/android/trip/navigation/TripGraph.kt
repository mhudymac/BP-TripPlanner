package kmp.android.trip.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
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
    object Edit : Destination(parent = this) {
        override val routeDefinition: String = "edit"
        internal const val tripIdArg = "tripId"

        override val arguments = listOf(
            navArgument(tripIdArg) { type = NavType.StringType }
        )

        internal class Args(
            val tripId: String
        ) {
            constructor(arguments: android.os.Bundle?) : this(
                requireNotNull(arguments?.getString(tripIdArg))
            )
        }
    }

    object Detail : Destination(parent = this) {
        override val routeDefinition: String = "detail"
        internal const val tripIdArg = "tripId"

        override val arguments = listOf(
            navArgument(tripIdArg) { type = NavType.StringType }
        )

        internal class Args(
            val tripId: String
        ) {
            constructor(arguments: android.os.Bundle?) : this(
                requireNotNull(arguments?.getString(tripIdArg))
            )
        }
    }
}
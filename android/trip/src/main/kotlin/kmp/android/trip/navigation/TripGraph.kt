package kmp.android.trip.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import kmp.android.shared.navigation.Destination
import kmp.android.shared.navigation.FeatureGraph

object TripGraph : FeatureGraph(parent = null) {
    override val path: String = "trips"

    object List : Destination(parent = this) {
        override val routeDefinition: String = "list"
    }
    object Edit : Destination(parent = this) {
        override val routeDefinition: String = "edit"
        internal const val tripIdArg = "tripId"

        override val arguments = listOf(
            navArgument(tripIdArg) { type = NavType.LongType }
        )

        internal class Args(
            val tripId: Long
        ) {
            constructor(arguments: android.os.Bundle?) : this(
                requireNotNull(arguments?.getLong(tripIdArg))
            )
        }
    }

    object Detail : Destination(parent = this) {
        override val routeDefinition: String = "detail"
        internal const val tripIdArg = "tripId"

        override val arguments = listOf(
            navArgument(tripIdArg) { type = NavType.LongType }
        )

        internal class Args(
            val tripId: Long
        ) {
            constructor(arguments: android.os.Bundle?) : this(
                requireNotNull(arguments?.getLong(tripIdArg))
            )
        }
    }

}
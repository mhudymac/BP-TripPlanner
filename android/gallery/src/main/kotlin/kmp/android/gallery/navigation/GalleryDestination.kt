package kmp.android.gallery.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import kmp.android.shared.navigation.Destination

data object GalleryDestination : Destination(parent = null) {
    override val routeDefinition: String = "gallery"
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
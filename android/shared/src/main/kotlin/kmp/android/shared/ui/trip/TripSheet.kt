package kmp.android.shared.ui.trip

import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kmp.android.shared.ui.trip.lists.TripOnThisDayList
import kmp.shared.domain.model.Trip

@Composable
fun TripsSheet(trips: List<Trip>, onTripClick: (Trip) -> Unit) {
    ModalDrawerSheet(
        modifier = Modifier.width(200.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        TripOnThisDayList(
            trips = trips,
            onTripClick = onTripClick
        )
    }
}

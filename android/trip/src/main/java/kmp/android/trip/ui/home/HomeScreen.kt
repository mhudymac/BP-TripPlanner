package kmp.android.trip.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import kmp.android.shared.core.util.get
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.ui.create.PlaceCard
import kmp.shared.domain.model.Trip
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toJavaInstant
import org.koin.androidx.compose.getViewModel
import kmp.android.trip.ui.home.HomeViewModel.ViewState as State


internal fun NavGraphBuilder.tripHomeRoute() {
    composableDestination(
        destination = TripGraph.Home
    ) {
        HomeScreenRoute()
    }
}

@Composable
internal fun HomeScreenRoute(
    viewModel: HomeViewModel = getViewModel()
){

    val loading by viewModel[State::loading].collectAsState(initial = false)
    val trip by viewModel[State::trip].collectAsState(initial = null)
    val error by viewModel[State::error].collectAsState(initial = "")

    trip?.let { HomeScreen(trip = it) }
}

@Composable
private fun HomeScreen(trip: Trip) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Upcoming Trip in ${
                Clock.System.now().daysUntil(
                    other = trip.date.atStartOfDayIn(TimeZone.currentSystemDefault()),
                    timeZone = TimeZone.currentSystemDefault()
                )
            } days",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Thin
        )

        Text(
            text = trip.name,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )

        LazyColumn {
            items(trip.itinerary) { place ->
                PlaceCard(place = place)
            }
        }
    }
}
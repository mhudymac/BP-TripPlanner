package kmp.android.trip.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import kmp.android.shared.core.ui.util.rememberLocationPermissionRequest
import kmp.android.shared.core.util.get
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.ui.create.PlaceCard
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Trip
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.daysUntil
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

    var location by remember { mutableStateOf<Location?>(null) }
    val permissionHandler = rememberLocationPermissionRequest()
    val locationPermissionGranted by permissionHandler.granted

    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted) {
            viewModel.getLocationFlow().collect {
                location = it
            }
        } else {
            permissionHandler.requestPermission()
        }
    }

    trip?.let {
        HomeScreen(
            trip = it,
            location = location,
            getDistanceBetween = viewModel::getDistanceBetween
        )
    }
}

@Composable
private fun HomeScreen(
    trip: Trip,
    location: Location?,
    getDistanceBetween: (Location, Location) -> Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
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

        PlaceCardListWithDistancesAndCurrent(
            trip = trip,
            location = location,
            getDistanceBetween = getDistanceBetween
        )
    }
}

@Composable
internal fun PlaceCardListWithDistancesAndCurrent(
    trip: Trip,
    location: Location?,
    getDistanceBetween: (Location, Location) -> Int
){
    var lastPlaceId = ""
    var lastActivePlaceId by remember{ mutableStateOf("") }
    var activePlaceId by remember{ mutableStateOf("") }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(trip.itinerary) { place ->

            val distance = location?.let { getDistanceBetween(it, place.location) }
            if(distance != null && distance < 200) {
                activePlaceId = place.id
            } else if(activePlaceId == place.id) {
                lastActivePlaceId = activePlaceId
                activePlaceId = ""
            }

            if(lastPlaceId.isNotEmpty() && place.id != trip.itinerary.first().id) {
                val duration = trip.distances[lastPlaceId to place.id]?.duration?.div(60)
                if(duration != null) {
                    DistanceCard(distanceInMinutes = duration, isActive = lastActivePlaceId == lastPlaceId && activePlaceId == "")
                }
            }

            PlaceCard(
                place = place,
                isActive = place.id == activePlaceId
            )

            lastPlaceId = place.id
        }
    }
}

@Composable
fun DistanceCard(
    distanceInMinutes: Long,
    isActive: Boolean = false
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Dot()
        Card(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .wrapContentWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
                containerColor = if (isActive) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceContainer,
                disabledContentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
            )
        ) {
            Text(
                text = "$distanceInMinutes minutes",
                modifier = Modifier.padding(8.dp),
            )
        }
        Dot()
    }
}

@Composable
fun Dot() {
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
    )
}
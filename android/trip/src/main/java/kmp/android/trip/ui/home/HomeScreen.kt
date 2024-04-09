package kmp.android.trip.ui.home

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import kmp.android.shared.core.ui.util.rememberCameraManager
import kmp.android.shared.core.ui.util.rememberCameraPermissionRequest
import kmp.android.shared.core.ui.util.rememberLocationPermissionRequest
import kmp.android.shared.core.util.get
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.ui.create.PlaceCard
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import kmp.shared.system.Log
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.daysUntil
import org.koin.androidx.compose.getViewModel
import java.io.IOException
import kmp.android.trip.ui.home.HomeViewModel.ViewState as State


internal fun NavGraphBuilder.tripHomeRoute() {
    composableDestination(
        destination = TripGraph.Home,
    ) {
        HomeScreenRoute()
    }
}

@Composable
internal fun HomeScreenRoute(
    viewModel: HomeViewModel = getViewModel(),
){

    val loading by viewModel[State::loading].collectAsState(initial = false)
    val trip by viewModel[State::trip].collectAsState(initial = null)
    val error by viewModel[State::error].collectAsState(initial = "")
    val images by viewModel[State::images].collectAsState(initial = emptyList())

    var location by remember { mutableStateOf<Location?>(null) }

    val locationPermissionHandler = rememberLocationPermissionRequest()
    val locationPermissionGranted by locationPermissionHandler.granted

    val cameraPermissionHandler = rememberCameraPermissionRequest()
    val cameraPermissionGranted by cameraPermissionHandler.granted

    val cameraManager = rememberCameraManager {
        viewModel.addUserPhoto(it.toString())
    }

    val context = LocalContext.current

    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted) {
            viewModel.getLocationFlow().collect {
                location = it
            }
        } else {
            locationPermissionHandler.requestPermission()
        }
    }

    LaunchedEffect(cameraPermissionGranted) {
        if (cameraPermissionGranted)
            cameraManager.launch()
    }

    if(loading){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }

    trip?.let {
        HomeScreen(
            trip = it,
            location = location,
            getDistanceBetween = viewModel::getDistanceBetween,
            changeActivePlace = viewModel::activePlaceId::set,
            onPlaceClick = { place ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${place.location.latitude},${place.location.longitude}&mode=w"))
                context.startActivity(intent)
            },
            onPlaceCameraClick = {
                if(cameraPermissionGranted) {
                    cameraManager.launch()
                } else {
                    cameraPermissionHandler.requestPermission()
                }
            },
            images = images,
        )
    }
}

@Composable
private fun HomeScreen(
    trip: Trip,
    location: Location?,
    getDistanceBetween: (Location, Location) -> Int,
    changeActivePlace: (String) -> Unit,
    onPlaceClick: (Place) -> Unit,
    onPlaceCameraClick: () -> Unit,
    images: List<Uri>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(
            text = "Upcoming Trip in ${
                Clock.System.now().daysUntil(
                    other = trip.date.atStartOfDayIn(TimeZone.currentSystemDefault()),
                    timeZone = TimeZone.currentSystemDefault(),
                )
            } days",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Thin,
        )

        Text(
            text = trip.name,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
        )

        PlaceCardListWithDistancesAndCurrent(
            trip = trip,
            location = location,
            getDistanceBetween = getDistanceBetween,
            changeActivePlace = changeActivePlace,
            onPlaceClick = onPlaceClick,
            onPlaceCameraClick = onPlaceCameraClick,
            images = images,
        )
    }
}

@Composable
internal fun PlaceCardListWithDistancesAndCurrent(
    trip: Trip,
    location: Location?,
    getDistanceBetween: (Location, Location) -> Int = { _, _ -> 0 },
    changeActivePlace: (String) -> Unit = {},
    onPlaceClick: (Place) -> Unit = {},
    onPlaceCameraClick: () -> Unit = {},
    images: List<Uri> = emptyList(),
){
    var lastActivePlaceId by remember{ mutableStateOf("") }
    var activePlaceId by remember{ mutableStateOf("") }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(trip.itinerary.windowed(size = 2, step = 1, partialWindows = true)) { places ->
            val currentPlace = places.first()
            val nextPlace = places.getOrNull(1)

            val distance = location?.let { getDistanceBetween(it, currentPlace.location) }
            if(distance != null && distance < 200) {
                activePlaceId = currentPlace.id
                changeActivePlace(currentPlace.id)
            } else if(activePlaceId == currentPlace.id) {
                lastActivePlaceId = activePlaceId
                activePlaceId = ""
            }

            PlaceCard(
                place = currentPlace,
                onClick = {
                    onPlaceClick(currentPlace)
                },
                onCameraClick = onPlaceCameraClick,
                isActive = currentPlace.id == activePlaceId,
                images = images,
            )

            if(nextPlace != null) {
                val duration = trip.distances[currentPlace.id to nextPlace.id]?.duration?.div(60)
                if(duration != null) {
                    DistanceCard(distanceInMinutes = duration, isActive = currentPlace.id == lastActivePlaceId && activePlaceId == "")
                }
            }
        }
    }
}

@Composable
fun DistanceCard(
    distanceInMinutes: Long,
    isActive: Boolean = false,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Dot()
        Card(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .wrapContentWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardColors(
                contentColor = if (isActive) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurface,
                containerColor = if (isActive) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                disabledContentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
            ),
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
            .background(MaterialTheme.colorScheme.primaryContainer),
    )
}

package kmp.android.trip.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import kmp.android.shared.core.ui.util.rememberPreciseLocationPermissionRequest
import kmp.android.shared.core.util.get
import kmp.android.shared.extension.distanceTo
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.ui.create.ActivePlaceCard
import kmp.android.trip.ui.create.PlaceCard
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.daysUntil
import org.koin.androidx.compose.getViewModel
import kmp.android.trip.ui.home.HomeViewModel.ViewState as State


internal fun NavGraphBuilder.tripHomeRoute(
    navigateToCreateScreen: () -> Unit
) {
    composableDestination(
        destination = TripGraph.Home,
    ) {
        HomeScreenRoute(navigateToCreateScreen = navigateToCreateScreen)
    }
}

@Composable
internal fun HomeScreenRoute(
    viewModel: HomeViewModel = getViewModel(),
    navigateToCreateScreen: () -> Unit,
){

    val loading by viewModel[State::loading].collectAsState(initial = true)
    val trip by viewModel[State::trip].collectAsState(initial = null)
    val error by viewModel[State::error].collectAsState(initial = "")
    val images by viewModel[State::images].collectAsState(initial = emptyList())
    val isTripActive by viewModel[State::isActive].collectAsState(initial = false)

    var location by remember { mutableStateOf<Location?>(null) }

    val locationPermissionHandler = rememberPreciseLocationPermissionRequest()
    val locationPermissionGranted by locationPermissionHandler.granted

    val cameraPermissionHandler = rememberCameraPermissionRequest()
    val cameraPermissionGranted by cameraPermissionHandler.granted

    val cameraManager = rememberCameraManager {
        viewModel.addUserPhoto(it.toString())
    }

    val scrollState = rememberLazyListState()
    val isFloatingButtonExpanded = remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset <= 0 } }

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

    var showDialog by remember { mutableStateOf(false) }

    if(showDialog) {
        FinishTripAlertDialog(
            onConfirm = { viewModel.finishTrip(); showDialog = false },
            onDismiss = { showDialog = false },
        )
    }

    if(loading){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    } else {
        if(trip != null){
            Scaffold(
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = { if(isTripActive) Text("Finish Trip") else Text("Start Trip") },
                        icon = { if(isTripActive) Icon(Icons.Default.Done, contentDescription = "Finish Trip") else Icon(Icons.Default.PlayArrow, contentDescription = "Start trip") },
                        onClick = { if(isTripActive) showDialog = true else viewModel.startTrip() },
                        expanded = isFloatingButtonExpanded.value,
                    )
                },
            ) {
                HomeScreen(
                    trip = trip!!,
                    location = location,
                    changeActivePlace = viewModel::activePlaceId::set,
                    onPlaceClick = { place ->
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                if(isTripActive)
                                    "google.navigation:q=${place.location.latitude},${place.location.longitude}&mode=w"
                                else
                                    place.googleMapsUri
                            )
                        )
                        context.startActivity(intent)
                    },
                    onPlaceCameraClick = {
                        if(isTripActive) {
                            if (cameraPermissionGranted) {
                                cameraManager.launch()
                            } else {
                                cameraPermissionHandler.requestPermission()
                            }
                        }
                    },
                    images = if(isTripActive) images else emptyList(),
                    isTripActive = isTripActive,
                    scrollState = scrollState,
                    padding = it
                )
            }
        } else {
            EmptyHomeScreen(
                navigateToCreateScreen
            )
        }
    }
}

@Composable
private fun FinishTripAlertDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text("Do you really want to finish trip?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@Composable
private fun EmptyHomeScreen(
    navigateToCreateTrip: () -> Unit,
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "No trip found",
            style = MaterialTheme.typography.titleLarge,
        )
        Button(
            onClick = navigateToCreateTrip,
        ) {
            Icon(Icons.Default.AddCircleOutline, contentDescription = "Create Trip", modifier = Modifier.padding(end = 8.dp))
            Text(
                text = "Create new trip",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun HomeScreen(
    trip: Trip,
    location: Location?,
    changeActivePlace: (String) -> Unit,
    onPlaceClick: (Place) -> Unit,
    onPlaceCameraClick: () -> Unit,
    images: List<Uri>,
    isTripActive: Boolean,
    scrollState: LazyListState,
    padding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if(isTripActive)
                    "Today"
                else
                    "Upcoming Trip in ${
                        Clock.System.now().daysUntil(
                            other = trip.date.atStartOfDayIn(TimeZone.currentSystemDefault()),
                            timeZone = TimeZone.currentSystemDefault()
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

        if(isTripActive) {
            ActivePlaceTripList(
                trip = trip,
                location = location,
                changeActivePlace = changeActivePlace,
                onPlaceClick = onPlaceClick,
                onPlaceCameraClick = onPlaceCameraClick,
                images = images,
                scrollState = scrollState
            )
        } else {
            InactiveTripPlaceList(
                trip = trip,
                onPlaceClick = onPlaceClick,
                scrollState = scrollState
            )

        }
    }
}

@Composable
internal fun ActivePlaceTripList(
    trip: Trip,
    location: Location?,
    changeActivePlace: (String) -> Unit,
    onPlaceClick: (Place) -> Unit,
    onPlaceCameraClick: () -> Unit,
    images: List<Uri>,
    scrollState: LazyListState,
){
    var lastActivePlaceId by remember{ mutableStateOf("") }
    var activePlaceId by remember{ mutableStateOf("") }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        state = scrollState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(trip.itinerary.windowed(size = 2, step = 1, partialWindows = true)) { places ->
            val currentPlace = places.first()
            val nextPlace = places.getOrNull(1)

            val distance = location?.distanceTo(currentPlace.location)
            if(distance != null && distance < 200) {
                activePlaceId = currentPlace.id
                changeActivePlace(currentPlace.id)
            } else if(activePlaceId == currentPlace.id) {
                lastActivePlaceId = activePlaceId
                activePlaceId = ""
            }

            if(activePlaceId == currentPlace.id) {
                ActivePlaceCard(
                    place = currentPlace,
                    onClick = { onPlaceClick(currentPlace) },
                    onCameraClick = onPlaceCameraClick,
                    images = images,
                )
            } else {
                PlaceCard(
                    place = currentPlace,
                    onClick = { onPlaceClick(currentPlace) },
                )
            }

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
internal fun InactiveTripPlaceList(
    trip: Trip,
    onPlaceClick: (Place) -> Unit,
    scrollState: LazyListState,
){
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        state = scrollState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(trip.itinerary.windowed(size = 2, step = 1, partialWindows = true)) { places ->
            val currentPlace = places.first()
            val nextPlace = places.getOrNull(1)

            PlaceCard(
                place = currentPlace,
                onClick = { onPlaceClick(currentPlace) },
            )

            if(nextPlace != null) {
                val duration = trip.distances[currentPlace.id to nextPlace.id]?.duration?.div(60)
                if(duration != null) {
                    DistanceCard(distanceInMinutes = duration)
                }
            }
        }
    }
}

@Composable
internal fun DistanceCard(
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
internal fun Dot() {
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
    )
}

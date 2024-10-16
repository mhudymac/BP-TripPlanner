package kmp.android.home.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import kmp.android.home.navigation.HomeDestination
import kmp.android.home.viewmodel.HomeViewModel
import kmp.android.shared.core.ui.util.rememberCameraManager
import kmp.android.shared.core.ui.util.rememberCameraPermissionRequest
import kmp.android.shared.core.ui.util.rememberLocationPermissionRequest
import kmp.android.shared.core.util.get
import kmp.android.shared.extension.daysUntil
import kmp.android.shared.navigation.composableDestination
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.getViewModel
import kmp.android.shared.R
import kmp.android.shared.extension.showIn
import kmp.android.shared.ui.loading.FullScreenLoading
import kmp.android.shared.ui.place.lists.ActivePlaceList
import kmp.android.shared.ui.place.lists.InactivePlaceList
import kmp.android.shared.ui.trip.FinishTripAlertDialog
import kmp.android.shared.ui.trip.TripsSheet
import kmp.android.home.viewmodel.HomeViewModel.ViewState as State

fun NavController.navigateToHomeScreen() {
    navigate(HomeDestination())
}

internal fun NavGraphBuilder.homeRoute(
    navigateToCreateScreen: () -> Unit
) {
    composableDestination(
        destination = HomeDestination,
    ) {
        HomeScreenRoute(navigateToCreateScreen = navigateToCreateScreen)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreenRoute(
    viewModel: HomeViewModel = getViewModel(),
    navigateToCreateScreen: () -> Unit,
){
    val loading by viewModel[State::loading].collectAsState(initial = true)
    val trip by viewModel[State::trip].collectAsState(initial = null)
    val trips by viewModel[State::trips].collectAsState(initial = emptyList())
    val isTripActive by viewModel[State::isActive].collectAsState(initial = false)
    val locationPermissionHandler = rememberLocationPermissionRequest()
    val locationPermissionGranted by locationPermissionHandler.granted

    val cameraPermissionHandler = rememberCameraPermissionRequest()
    val cameraPermissionGranted by cameraPermissionHandler.granted

    val cameraManager = rememberCameraManager {
        viewModel.addUserPhoto(it.toString())
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val scrollState = rememberLazyListState()
    val isFloatingButtonExpanded = remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset <= 0 } }

    val snackbarHost = remember { SnackbarHostState() }

    viewModel.errorFlow showIn snackbarHost

    val context = LocalContext.current

    LaunchedEffect(locationPermissionGranted) {
        if (!locationPermissionGranted) {
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
        FullScreenLoading(text = stringResource(id = R.string.trip_loading))
    } else {
        if(trip != null) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    TripsSheet(
                        trips = trips,
                        onTripClick = {
                            viewModel.setActiveTrip(it)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                },
            ) {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(trip!!.name, maxLines = 1, style = MaterialTheme.typography.titleMedium)
                            },
                            navigationIcon = {
                                DrawerIconBadge(
                                    onClick = { coroutineScope.launch { drawerState.open() } },
                                    tripCount = trips.size,
                                    icon = {
                                        Icon(
                                            Icons.Default.Menu,
                                            contentDescription = stringResource(id = R.string.drawer_menu),
                                        )
                                    },
                                )
                            },
                        )
                    },
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            text = { if (isTripActive) Text(stringResource(id = R.string.finish_trip)) else Text(stringResource(id = R.string.start_trip)) },
                            icon = {
                                if (isTripActive) Icon(
                                    Icons.Default.Done,
                                    contentDescription = stringResource(id = R.string.finish_trip),
                                ) else Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = stringResource(id = R.string.start_trip),
                                )
                            },
                            onClick = {
                                if (isTripActive) showDialog = true else viewModel.startTrip()
                            },
                            expanded = isFloatingButtonExpanded.value,
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHost) },
                ) {
                    HomeScreen(
                        trip = trip!!,
                        onPlaceClick = { place ->
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(if (isTripActive) "google.navigation:q=${place.location.latitude},${place.location.longitude}&mode=w"  else place.googleMapsUri)
                            )
                            context.startActivity(intent)
                        },
                        onPlaceCameraClick = {
                            if (isTripActive) {
                                if (cameraPermissionGranted) {
                                    cameraManager.launch()
                                } else {
                                    cameraPermissionHandler.requestPermission()
                                }
                            }
                        },
                        isTripActive = isTripActive,
                        scrollState = scrollState,
                        padding = it
                    )
                }
            }
        } else {
            EmptyHomeScreen(
                navigateToCreateScreen
            )
        }
    }
}

@Composable
fun DrawerIconBadge(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tripCount: Int,
    icon: @Composable () -> Unit,
) {
    Box(modifier = modifier.padding(12.dp)) {
        IconButton(onClick =  onClick ) {
            icon()
        }
        if(tripCount > 1){
            Badge(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(text = tripCount.toString())
            }
        }
    }
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
            text = stringResource(id = R.string.no_trip),
            style = MaterialTheme.typography.titleLarge,
        )

        Button(
            onClick = navigateToCreateTrip,
        ) {
            Icon(Icons.Default.AddCircleOutline, contentDescription = stringResource(id = R.string.create), modifier = Modifier.padding(end = 8.dp))
            Text(
                text = stringResource(id = R.string.create),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun HomeScreen(
    trip: Trip,
    onPlaceClick: (Place) -> Unit,
    onPlaceCameraClick: () -> Unit,
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
        HomeScreenDateText(trip.date, isTripActive)

        if(isTripActive) {
            ActivePlaceList(
                trip = trip,
                onPlaceClick = onPlaceClick,
                onPlaceCameraClick = onPlaceCameraClick,
                scrollState = scrollState
            )
        } else {
            InactivePlaceList(
                trip = trip,
                onPlaceClick = onPlaceClick,
                scrollState = scrollState
            )

        }
    }
}

@Composable
fun HomeScreenDateText(
    date: LocalDate,
    isTripActive: Boolean
){
    Text(
        text = if (isTripActive) {
            stringResource(id = R.string.today)
        } else {
            stringResource(id = R.string.upcoming_in) + " " + date.daysUntil + " " + stringResource(id = R.string.days)
        },
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Thin,
        textAlign = TextAlign.Center,
    )
}
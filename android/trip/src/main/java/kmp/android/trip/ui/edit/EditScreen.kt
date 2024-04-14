package kmp.android.trip.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Reorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import kmp.android.shared.core.ui.util.rememberLocationPermissionRequest
import kmp.android.shared.core.util.get
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.ui.components.AddPlaceCard
import kmp.android.trip.ui.components.ComponentWithLabel
import kmp.android.trip.ui.components.EmptyPlaceCard
import kmp.android.trip.ui.components.FullScreenLoading
import kmp.android.trip.ui.components.OptimiseTripDialog
import kmp.android.trip.ui.components.OverlayLoading
import kmp.android.trip.ui.components.PlaceCard
import kmp.android.trip.ui.components.SelectDateComponent
import kmp.android.trip.ui.components.TopBar
import kmp.android.trip.ui.components.TripDateButtonComponent
import kmp.android.trip.ui.components.TripNameTextFieldComponent
import kmp.android.trip.ui.components.TwoCardButtons
import kmp.android.trip.ui.components.lists.PlaceReorderingList
import kmp.android.trip.ui.search.BottomBarSearch
import kmp.shared.domain.model.Place
import org.koin.androidx.compose.getViewModel
import java.time.LocalDate
import kmp.android.shared.R
import kmp.android.trip.ui.edit.EditViewModel.ViewState as State

fun NavController.navigateToEditScreen(tripId: Long) {
    navigate(TripGraph.Edit(tripId))
}

fun NavController.navigateToCreateScreen() {
    navigate(TripGraph.Edit(-1L))
}

internal fun NavGraphBuilder.editScreenRoute(navigateUp: () -> Unit) {
    composableDestination(
        destination = TripGraph.Edit
    ) { navBackStackEntry ->
        val args = TripGraph.Edit.Args(navBackStackEntry.arguments)
        EditRoute(
            tripId = args.tripId,
            navigateUp = navigateUp
        )
    }
}

@Composable
internal fun EditRoute (
    navigateUp: () -> Unit,
    tripId: Long = -1L,
    viewModel: EditViewModel = getViewModel()
) {
    val name by viewModel[State::name].collectAsState("")
    val date by viewModel[State::date].collectAsState(null)
    val itinerary by viewModel[State::itinerary].collectAsState(emptyList())
    val reordering by viewModel[State::reordering].collectAsState(false)
    val saveSuccess by viewModel[State::saveSuccess].collectAsState(false)
    val error by viewModel[State::error].collectAsState(Pair("", 0))

    val locationLoading by viewModel[State::locationLoading].collectAsState(false)
    val screenLoading by viewModel[State::screenLoading].collectAsState(false)
    val savingLoading by viewModel[State::savingLoading].collectAsState(false)

    var showOptimiseDialog by remember { mutableStateOf(false) }

    val permissionHandler = rememberLocationPermissionRequest()
    val locationPermissionGranted by permissionHandler.granted

    val snackHost = remember { SnackbarHostState() }

    LaunchedEffect(tripId) {
        viewModel.getTrip(tripId)
    }

    LaunchedEffect(error) {
        if(error.first.isNotEmpty()) {
            snackHost.showSnackbar(error.first)
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            navigateUp()
        }
    }


    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted)
            viewModel.getLocation()
    }

    if(showOptimiseDialog) {
        OptimiseTripDialog(
            onConfirm = { showOptimiseDialog = false; viewModel.saveTrip(true) },
            onDismiss = { showOptimiseDialog = false; viewModel.saveTrip(false) }
        )
    }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackHost )},
        topBar = {
            TopBar(
                title = if(tripId > 0L) stringResource(id = R.string.edit) else stringResource(id = R.string.create),
                onBackArrow = {},
                showBackArrow = false,
                actions = {
                    Switch(
                        checked = reordering, onCheckedChange = { viewModel.toggleReordering() },
                        thumbContent = {
                            Icon(
                                imageVector = Icons.Outlined.Reorder,
                                contentDescription = stringResource(id = R.string.reorder_icon),
                            )
                        },
                    )
                    IconButton(onClick = {
                        if(tripId == -1L){
                            showOptimiseDialog = true
                        } else {
                            viewModel.saveTrip(false)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = stringResource(id = R.string.save),
                        )
                    }
                },
            )

        },
    ) {
        if(screenLoading){
            FullScreenLoading(text = stringResource(id = R.string.trip_loading))
        } else if (reordering) {
            PlaceReorderingList(
                itinerary = itinerary,
                updateTripOrder = viewModel::updateTripOrder,
                padding = it
            )
        }
        else {
            CreateScreen(
                name = name,
                date = date,
                itinerary = itinerary,
                loading = locationLoading,
                padding = it,
                onNameChange = viewModel::updateName,
                onDateSelected = viewModel::updateDate,
                onAddPlace = viewModel::addPlace,
                onRemovePlace = viewModel::removePlace,
                onCurrentLocationClick = {
                    if (locationPermissionGranted) {
                        viewModel.getLocation()
                    } else {
                        permissionHandler.requestPermission()
                    }
                }
            )
        }
    }

    if(savingLoading){
        OverlayLoading(text = stringResource(id = R.string.trip_saving))
    }
}

@Composable
internal fun CreateScreen(
    name: String,
    date: LocalDate?,
    itinerary: List<Place>,
    loading: Boolean,
    padding: PaddingValues,
    onNameChange: (String) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onAddPlace: (Place) -> Unit,
    onRemovePlace: (Place) -> Unit,
    onCurrentLocationClick: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showSearchBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        TripNameTextFieldComponent(name = name, onNameChange = onNameChange, focusManager = focusManager)

        TripDateButtonComponent(date = date, onShowDatePicker = { showDatePicker = true })

        ComponentWithLabel(label = stringResource(id = R.string.itinerary_lable)) {
            if(itinerary.isEmpty()){
                if(loading) {
                    EmptyPlaceCard(onClick = {}) {
                        CircularProgressIndicator()
                    }
                } else {
                    TwoCardButtons(
                        onClickLeft = { showSearchBottomSheet = true },
                        onClickRight = onCurrentLocationClick,
                        contentLeft = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(id = R.string.search,),
                            )
                            Text(text = stringResource(id = R.string.place_search))
                        },
                        contentRight = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = stringResource(id = R.string.current_location),
                            )
                            Text(text = stringResource(id = R.string.use_current_locaiton))
                        },
                    )
                }
            } else {
                LazyColumn(
                    reverseLayout = true,
                ) {
                    item {
                        AddPlaceCard(onClick = { showSearchBottomSheet = true })
                    }
                    items(itinerary.reversed()) { place ->
                        PlaceCard(
                            place = place,
                            onDeleteClick = { onRemovePlace(place) }
                        )
                    }
                }
            }
        }
    }

    if(showDatePicker) {
        SelectDateComponent(
            onDateSelected = { onDateSelected(it) },
            onDismiss = { showDatePicker = false},
        )
    }

    if (showSearchBottomSheet) {
        BottomBarSearch(
            onDismissRequest = { showSearchBottomSheet = false },
            onPlaceSelected = { selectedPlace ->
                onAddPlace(selectedPlace)
                showSearchBottomSheet = false
            },
            location = itinerary.firstOrNull()?.location
        )
    }
}
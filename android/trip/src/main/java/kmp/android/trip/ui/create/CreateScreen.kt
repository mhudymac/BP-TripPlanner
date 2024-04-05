package kmp.android.trip.ui.create

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.ImagePainter
import kmp.android.home.R
import kmp.android.shared.core.ui.util.rememberLocationPermissionRequest
import kmp.android.shared.core.util.get
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.ui.detail.TopBar
import kmp.android.trip.ui.search.SearchScreen
import kmp.shared.domain.model.Place
import org.koin.androidx.compose.getViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kmp.android.trip.ui.create.CreateViewModel.ViewState as State

fun NavController.navigateToCreateScreen() {
    navigate(TripGraph.Create())
}

internal fun NavGraphBuilder.createScreenRoute(navigateUp: () -> Unit) {
    composableDestination(
        destination = TripGraph.Create,
    ) {
        CreateRoute(navigateUp = navigateUp)
    }
}

@Composable
private fun CreateRoute(
    viewModel: CreateViewModel = getViewModel(),
    navigateUp: () -> Unit
) {
    val name by viewModel[State::name].collectAsState("")
    val date by viewModel[State::date].collectAsState(null)
    val start by viewModel[State::start].collectAsState(null)
    val itinerary by viewModel[State::itinerary].collectAsState(emptyList())
    val loading by viewModel[State::isLoading].collectAsState(false)
    val error by viewModel[State::error].collectAsState(Pair("",0))
    val saveSuccess by viewModel[State::saveSuccess].collectAsState(initial = false)

    val snackHost = remember { SnackbarHostState() }

    val permissionHandler = rememberLocationPermissionRequest()
    val locationPermissionGranted by permissionHandler.granted

    if(error.first.isNotEmpty()){
        LaunchedEffect(error) {
            snackHost.showSnackbar(error.first)
        }
    }

    LaunchedEffect(saveSuccess) {
        if(saveSuccess) {
            snackHost.showSnackbar("Trip saved")
            navigateUp()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackHost) },
        topBar = {
            TopBar(
                title = "Create Trip",
                onBackArrow = navigateUp
            ) {
                IconButton(onClick = { viewModel.saveTrip() }) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Save Trip"
                    )
                }
            }
        },
    ) { paddingValues ->
        CreateScreen(
            name = name,
            date = date,
            start = start,
            itinerary = itinerary,
            padding = paddingValues,
            onNameChange = { viewModel.updateName(it) },
            onDateSelected = { viewModel.updateDate(it) },
            onAddPlace = { viewModel.addPlace(it) },
            onCurrentLocationClick = {
                if (locationPermissionGranted) {
                    viewModel.getLocation()
                } else {
                    permissionHandler.requestPermission()
                    viewModel.getLocation()
                }
            },
        )
    }

}

@Composable
internal fun CreateScreen(
    name: String,
    date: LocalDate?,
    start: Place?,
    itinerary: List<Place>,
    padding: PaddingValues,
    onNameChange: (String) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onAddPlace: (Place) -> Unit,
    onCurrentLocationClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {

        val focusManager = LocalFocusManager.current
        var showDatePicker by remember { mutableStateOf(false) }
        var showSearchDialog by remember { mutableStateOf(false) }

        TripNameComponent(name = name, onNameChange = onNameChange, focusManager = focusManager)

        TripDateComponent(date = date, onShowDatePicker = { showDatePicker = true })

        ComponentWithLabel(label = "Start Place") {
            if(start != null) {
                PlaceCard(start)
            } else {
                Column {
                    EmptyPlaceCard(onClick = { showSearchDialog = true })
                        Button(
                            onClick = onCurrentLocationClick
                        ) {
                        Text("Use Current Location")
                    }
                }
            }
        }

        start?.let {
            ComponentWithLabel(label = "Itinerary") {
                LazyColumn {
                    item {
                        EmptyPlaceCard { showSearchDialog = true }
                    }
                    items(itinerary.reversed()) { place ->
                        PlaceCard(place = place)
                    }
                }
            }
        }

        if(showDatePicker) {
            SelectDate(
                onDateSelected = { onDateSelected(it) },
                onDismiss = { showDatePicker = false},
            )
        }

        if (showSearchDialog) {
            CardDialog(
                onDismiss = { showSearchDialog = false }
            ) {
                SearchScreen(
                    onPlaceSelected = { selectedPlace ->
                        onAddPlace(selectedPlace)
                        showSearchDialog = false
                    },
                    latLng = start?.let { Pair( it.latitude, it.longitude )}
                )
            }
        }
    }
}

@Composable
fun TripDateComponent(date: LocalDate?, onShowDatePicker: () -> Unit) {
    ComponentWithLabel(label = "Trip date") {
        OutlinedTextFieldLikeButton(
            text = date?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "Select Date",
            onClick = { onShowDatePicker() }
        )
    }
}

@Composable
fun TripNameComponent(name: String, onNameChange: (String) -> Unit, focusManager: FocusManager) {
    ComponentWithLabel("Trip name", padding = PaddingValues(0.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { onNameChange(it) },
            placeholder = { Text("Enter name") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            shape = MaterialTheme.shapes.medium,
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .height(52.dp)
                .width(LocalConfiguration.current.screenWidthDp.dp),
        )
    }
}

@Composable
internal fun ComponentWithLabel(
    label: String,
    padding: PaddingValues = PaddingValues(top = 16.dp),
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(text = "$label:", modifier = Modifier.padding(padding))
        content()
    }
}

@Composable
internal fun OutlinedTextFieldLikeButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .height(52.dp)
            .width(LocalConfiguration.current.screenWidthDp.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CardDialog(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .fillMaxHeight(0.66f),
            shape = RoundedCornerShape(16.dp),

            ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectDate(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis >= System.currentTimeMillis()
        }
    })

    val selectedDate = datePickerState.selectedDateMillis?.let {
        LocalDateTime.ofEpochSecond(it / 1000, 0, ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())).toLocalDate()
    } ?: LocalDate.now()

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                onDateSelected(selectedDate)
                onDismiss()
            }

            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@Composable
internal fun PlaceCard(
    place: Place,
    onClick: () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {},
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.large,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = place.photoUri,
                contentDescription = "Place Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(112.dp)
                    .padding(8.dp)
                    .clip(MaterialTheme.shapes.large),
                placeholder = painterResource(id = R.drawable.placeholder_view_vector),
                error = painterResource(id = R.drawable.placeholder_view_vector),

                )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = place.formattedAddress,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            trailingIcon()

        }
    }
}

@Composable
internal fun EmptyPlaceCard(onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(vertical = 8.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Place",
            )
            Text(text = "Add a place")
        }
    }
}
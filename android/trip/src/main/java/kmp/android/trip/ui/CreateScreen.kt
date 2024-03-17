package kmp.android.trip.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import kmp.android.shared.core.util.get
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.vm.CreateViewModel
import org.koin.androidx.compose.getViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kmp.android.trip.vm.CreateViewModel.ViewState as State

fun NavController.navigateToCreateScreen() {
    navigate(TripGraph.Create())
}

internal fun NavGraphBuilder.createScreenRoute(navigateToSearchScreen: () -> Unit) {
    composableDestination(
        destination = TripGraph.Create,
    ) {
        CreateScreen(navigateToSearchScreen = navigateToSearchScreen)
    }
}

@Composable
private fun CreateScreen(
    viewModel: CreateViewModel = getViewModel(),
    navigateToSearchScreen: () -> Unit,
) {
    val name by viewModel[State::name].collectAsState("")
    val date by viewModel[State::date].collectAsState(null)
    val start by viewModel[State::start].collectAsState(null)
    val itinerary by viewModel[State::itinerary].collectAsState(emptyList())
    val loading by viewModel[State::isLoading].collectAsState(false)
    val error by viewModel[State::error].collectAsState(null)

    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = name,
            onValueChange = { viewModel.updateName(it) },
            label = { Text("Name") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        )

        Button(onClick = { showDatePicker = true }) {
            Text(text = date?.format(  DateTimeFormatter.ofPattern("dd.MM.yyyy") ) ?: "Select Date")
        }

        if(showDatePicker) {
            MyDatePickerDialog(
                onDateSelected = { viewModel.updateDate(it) },
                onDismiss = { showDatePicker = false}
            )
        }


        var showDialog by remember { mutableStateOf(false) }

        Button(onClick = { showDialog = true}) {
            Text("Add Place")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Search for a place") },
                text = {
                    SearchScreen(
                        onPlaceSelected = { selectedPlace ->
                            viewModel.addPlace(selectedPlace)
                            showDialog = false
                        }
                    )
                },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Confirm")
                    }
                }
            )
        }

        LazyColumn {
            items(itinerary) { place ->
                Text(place.name)
            }
        }

        Button(onClick = {  }) {
            Text("Create Trip")
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis >= System.currentTimeMillis()
        }
    })

    val selectedDate = datePickerState.selectedDateMillis?.let {
        LocalDateTime.ofEpochSecond(it / 1000, 0, ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))
    } ?: LocalDateTime.now()

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
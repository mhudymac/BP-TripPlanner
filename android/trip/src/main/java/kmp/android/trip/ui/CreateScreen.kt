package kmp.android.trip.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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

internal fun NavGraphBuilder.createScreenRoute(navigateUp: () -> Unit) {
    composableDestination(
        destination = TripGraph.Create,
    ) {
        CreateScreen(navigateUp = navigateUp)
    }
}

@Composable
private fun CreateScreen(
    viewModel: CreateViewModel = getViewModel(),
    navigateUp: () -> Unit
) {
    val name by viewModel[State::name].collectAsState("")
    val date by viewModel[State::date].collectAsState(null)
    val start by viewModel[State::start].collectAsState(null)
    val itinerary by viewModel[State::itinerary].collectAsState(emptyList())
    val loading by viewModel[State::isLoading].collectAsState(false)
    val error by viewModel[State::error].collectAsState(Pair("",0))

    var showDatePicker by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val snackHost = remember { SnackbarHostState() }

    if(error.first.isNotEmpty()){
        LaunchedEffect(error) {
            snackHost.showSnackbar(error.first)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(onClick = { viewModel.saveTrip(); navigateUp() }) {
            Text("Create Trip")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
            Text(text =  date?.format(  DateTimeFormatter.ofPattern("dd.MM.yyyy") ) ?: "Select Date")
        }

        if(showDatePicker) {
            SelectDate(
                onDateSelected = { viewModel.updateDate(it) },
                onDismiss = { showDatePicker = false}
            )
        }

        Button(onClick = { showDialog = true}) {
            Text("Add Place")
        }

        if (showDialog) {
            CardDialog(
                onDismiss = { showDialog = false }
            ) {
                SearchScreen(
                    onPlaceSelected = { selectedPlace ->
                        viewModel.addPlace(selectedPlace)
                        showDialog = false
                    }
                )
            }
        }

        LazyColumn {
            item{
                ImageAndText(onclick =  {}, text = start?.name ?: "Start", imageUrl = start?.photoUri)
            }
            items(itinerary) { place ->
                ImageAndText(onclick =  {}, text = place.name, imageUrl = place.photoUri)
            }
        }

        SnackbarHost(snackHost, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun CardDialog(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalConfiguration.current.screenHeightDp.dp * 0.66f),
            shape = RoundedCornerShape(16.dp),

            ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDate(
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
package kmp.android.trip.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kmp.android.trip.ui.components.lists.TripOnThisDayList
import kmp.shared.domain.model.Trip
import kotlinx.datetime.toJavaLocalDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
internal fun FinishTripAlertDialog(
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
internal fun DeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text("Do you really want to delete this trip?") },
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
internal fun OptimiseTripDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { },
        text = { Text(
            text = "Our smart order algorithm can create the best itinerary tailored just for you!\nDo you want to use it?",
            textAlign = TextAlign.Center
        ) },
        icon = {
            Icon(
                Icons.Outlined.Lightbulb,
                contentDescription = "Smart order"
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}

@Composable
internal fun TripsSheet(trips: List<Trip>, onTripClick: (Trip) -> Unit) {
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


@Composable
fun TripDateButtonComponent(date: LocalDate?, onShowDatePicker: () -> Unit) {
    ComponentWithLabel(label = "Trip date") {
        OutlinedTextFieldLikeButton(
            text = date?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "Select Date",
            onClick = { onShowDatePicker() }
        )
    }
}

@Composable
fun TripNameTextFieldComponent(name: String, onNameChange: (String) -> Unit, focusManager: FocusManager) {
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
internal fun TripCard(
    trip: Trip,
    onClick: () -> Unit,
    button: @Composable () -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.large,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = trip.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = trip.date.toJavaLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            button()
        }
    }
}
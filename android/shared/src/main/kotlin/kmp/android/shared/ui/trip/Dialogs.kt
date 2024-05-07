package kmp.android.shared.ui.trip

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import kmp.android.shared.R

@Composable
fun FinishTripAlertDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text(stringResource(id = R.string.finish_trip_dialog)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(id = R.string.affirmative))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.negative))
            }
        },
    )
}

@Composable
fun DeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text(stringResource(id = R.string.delete_trip_dialog)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(id = R.string.affirmative))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.negative))
            }
        },
    )
}

@Composable
fun OptimiseTripDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(id = R.string.optimize_dialog),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(id = R.string.do_you_want_to_use_it),
                    textAlign = TextAlign.Center
                )
            }
        },
        icon = {
            Icon(
                Icons.Outlined.Lightbulb,
                contentDescription = stringResource(id = R.string.lightbulb),
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(id = R.string.affirmative))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.negative))
            }
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    )
}
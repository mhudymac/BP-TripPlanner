package kmp.android.trip.screens.edit.helpers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kmp.android.shared.R
import kmp.android.shared.ui.helpers.ComponentWithLabel
import kmp.android.shared.ui.helpers.OutlinedTextFieldLikeButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TripDateButtonComponent(date: LocalDate?, onShowDatePicker: () -> Unit) {
    ComponentWithLabel(label = stringResource(id = R.string.trip_date_label)) {
        OutlinedTextFieldLikeButton(
            text = date?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                ?: stringResource(id = R.string.select_date),
            onClick = { onShowDatePicker() },
        )
    }
}

@Composable
fun TripNameTextFieldComponent(name: String, onNameChange: (String) -> Unit, focusManager: FocusManager) {
    ComponentWithLabel(stringResource(id = R.string.trip_name_label), padding = PaddingValues(0.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { onNameChange(it) },
            placeholder = { Text(stringResource(id = R.string.enter_name)) },
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


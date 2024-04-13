package kmp.android.trip.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectDateComponent(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val selectedDate = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            val currentDate = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDate()
            return !selectedDate.isBefore(currentDate)
        }
    })

    val selectedDate = datePickerState.selectedDateMillis?.let {
        LocalDateTime.ofEpochSecond(it / 1000, 0, ZoneId.systemDefault().rules.getOffset(
            LocalDateTime.now())).toLocalDate()
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
internal fun FullScreenLoading( text: String = "" ) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        if(text.isNotEmpty()) {
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
internal fun OverlayLoading( text: String = "" ) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors().copy( containerColor = Color.Black.copy(alpha = 0.5f)),
    ) {
        FullScreenLoading(text)
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
internal fun TwoCardButtons(
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
    contentLeft: @Composable () -> Unit,
    contentRight: @Composable () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EmptyPlaceCard(
            onClick = onClickLeft,
            modifier = Modifier
                .weight(0.5f)
        ){
            contentLeft()
        }

        EmptyPlaceCard(
            onClick = onClickRight,
            modifier = Modifier.weight(0.5f)
        ){
            contentRight()
        }
    }
}

@Composable
internal fun DistanceCard(
    distanceInMinutes: Long,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopBar(
    title: String,
    onBackArrow: () -> Unit,
    showBackArrow: Boolean = true,
    actions: @Composable () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackArrow) {
                IconButton(onClick = onBackArrow) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = ""
                    )
                }
            }
        },
        actions = { actions() }
    )
}
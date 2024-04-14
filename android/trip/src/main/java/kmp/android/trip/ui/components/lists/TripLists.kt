package kmp.android.trip.ui.components.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kmp.android.shared.extension.localizedString
import kmp.android.trip.ui.components.TripCard
import kmp.shared.domain.model.Trip
import kmp.android.shared.R
import kmp.android.trip.ui.components.FullScreenLoading

@Composable
internal fun TripOnThisDayList(
    trips: List<Trip>,
    onTripClick: (Trip) -> Unit,
) {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            colors = CardDefaults.cardColors().copy( containerColor = MaterialTheme.colorScheme.surface ),
        ) {
            Text(
                text = stringResource(id = R.string.trips_on) + " " + "${trips.first().date.localizedString}:",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )
        }
        HorizontalDivider( modifier = Modifier.padding (bottom = 8.dp))
        LazyColumn {
            items(trips) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onTripClick(it) },
                ) {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
internal fun TripListWithButtons(
    trips: List<Trip>,
    onTripClick: (Long) -> Unit,
    onTripButtonClick: (Trip) -> Unit,
    loading: Boolean,
    scrollState: LazyListState,
    buttonContent: @Composable () -> Unit = {}
) {
    if(loading){
        FullScreenLoading()
    } else if (trips.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.no_trips),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 8.dp),
            state = scrollState,
        ) {
            items(trips) { trip ->
                TripCard(trip = trip, onClick = { onTripClick(trip.id) }) {
                    OutlinedButton(
                        onClick = { onTripButtonClick(trip) },
                        shape = MaterialTheme.shapes.medium,
                        border = null,
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            buttonContent()
                        }
                    }
                }
            }
        }
    }
}
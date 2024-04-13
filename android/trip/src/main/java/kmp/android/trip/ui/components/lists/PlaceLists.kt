package kmp.android.trip.ui.components.lists

import android.net.Uri
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import kmp.android.trip.ui.components.ActivePlaceCard
import kmp.android.trip.ui.components.ComponentWithLabel
import kmp.android.trip.ui.components.DistanceCard
import kmp.android.trip.ui.components.PlaceCard
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyColumnState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaceReorderingList(
    itinerary: List<Place>,
    updateTripOrder: (List<Place>) -> Unit,
    padding: PaddingValues
) {
    val view = LocalView.current
    var places by remember { mutableStateOf(itinerary) }
    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState =
        rememberReorderableLazyColumnState(lazyListState) { from, to ->
            places = places.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
            updateTripOrder(places)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 8.dp)
    ) {
        ComponentWithLabel(label = "Itinerary") {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(places, key = { it.id }) { place ->
                    ReorderableItem(reorderableLazyColumnState, key = place.id) {
                        PlaceCard(
                            place,
                            trailingIcon = {
                                Icon(
                                    modifier = Modifier
                                        .draggableHandle(
                                            onDragStarted = {
                                                view.performHapticFeedback(
                                                    HapticFeedbackConstants.LONG_PRESS
                                                )
                                            },
                                            onDragStopped = {
                                                view.performHapticFeedback(
                                                    HapticFeedbackConstants.VIRTUAL_KEY
                                                )
                                            }
                                        )
                                        .size(32.dp)
                                        .padding(end = 8.dp),
                                    imageVector = Icons.Rounded.Menu,
                                    contentDescription = "Reorder"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun InactiveTripPlaceList(
    trip: Trip,
    onPlaceClick: (Place) -> Unit,
    scrollState: LazyListState,
){
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        state = scrollState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(trip.itinerary.windowed(size = 2, step = 1, partialWindows = true)) { places ->
            val currentPlace = places.first()
            val nextPlace = places.getOrNull(1)

            PlaceCard(
                place = currentPlace,
                onClick = { onPlaceClick(currentPlace) },
            )

            if(nextPlace != null) {
                val duration = trip.distances[currentPlace.id to nextPlace.id]?.duration?.div(60)
                if(duration != null) {
                    DistanceCard(distanceInMinutes = duration)
                }
            }
        }
    }
}


@Composable
internal fun ActivePlaceTripList(
    trip: Trip,
    onPlaceClick: (Place) -> Unit,
    onPlaceCameraClick: () -> Unit,
    scrollState: LazyListState,
){
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        state = scrollState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(trip.itinerary.windowed(size = 2, step = 1, partialWindows = true)) { places ->
            val currentPlace = places.first()
            val nextPlace = places.getOrNull(1)

            if(trip.activePlace == currentPlace.id) {
                ActivePlaceCard(
                    place = currentPlace,
                    onClick = { onPlaceClick(currentPlace) },
                    onCameraClick = onPlaceCameraClick,
                    images = trip.photos.filter { it.placeId == currentPlace.id }.map { Uri.parse(it.photoUri) }
                )
            } else {
                PlaceCard(
                    place = currentPlace,
                    onClick = { onPlaceClick(currentPlace) },
                )
            }

            if(nextPlace != null) {
                val duration = trip.distances[currentPlace.id to nextPlace.id]?.duration?.div(60)
                if(duration != null) {
                    DistanceCard(distanceInMinutes = duration)
                }
            }
        }
    }
}
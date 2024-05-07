package kmp.android.shared.ui.place.lists

import android.content.Intent
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kmp.android.shared.R
import kmp.android.shared.ui.distance.DistanceCard
import kmp.android.shared.ui.helpers.ComponentWithLabel
import kmp.android.shared.ui.place.ActivePlaceCard
import kmp.android.shared.ui.place.PlaceCard

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
        ComponentWithLabel(label = stringResource(id = R.string.itinerary_lable)) {
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
                                    contentDescription = stringResource(R.string.reorder_icon)
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
fun InactivePlaceList(
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
fun ActivePlaceList(
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

@Composable
fun PlacesWithSearchList(
    places: List<Place>,
    onPlaceClick: (Place) -> Unit,
    onSearchButtonClick: (Place) -> Unit,
){
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(horizontal = 8.dp),
    ) {
        items(places) { place ->
            PlaceCard(
                place = place,
                onClick = { onPlaceClick(place) },
                trailingIcon = {
                    IconButton(
                        onClick = { onSearchButtonClick(place) }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                    }
                },
            )
        }
    }
}
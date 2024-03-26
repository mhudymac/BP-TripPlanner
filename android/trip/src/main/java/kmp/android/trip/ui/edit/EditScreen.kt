package kmp.android.trip.ui.edit

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import kmp.android.shared.core.util.get
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.ui.create.CreateScreen
import kmp.android.trip.ui.create.PlaceCard
import kmp.android.trip.ui.detail.TopBar
import kmp.shared.domain.model.Place
import kotlinx.datetime.toJavaLocalDate
import org.koin.androidx.compose.getViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyColumnState
import kmp.android.trip.ui.edit.EditViewModel.ViewState as State

fun NavController.navigateToEditScreen(tripId: String, navigateUp: () -> Unit) {
    navigate(TripGraph.Edit(tripId))
}

internal fun NavGraphBuilder.editScreenRoute(navigateUp: () -> Unit) {
    composableDestination(
        destination = TripGraph.Edit
    ) { navBackStackEntry ->
        val args = TripGraph.Detail.Args(navBackStackEntry.arguments)
        DetailEditRoute(
            tripId = args.tripId,
            navigateUp = navigateUp
        )
    }
}

@Composable
internal fun DetailEditRoute (
    tripId: String,
    navigateUp: () -> Unit,
    viewModel: EditViewModel = getViewModel()
) {
    val name by viewModel[State::name].collectAsState("")
    val date by viewModel[State::date].collectAsState(null)
    val start by viewModel[State::start].collectAsState(null)
    val itinerary by viewModel[State::itinerary].collectAsState(emptyList())
    val reordering by viewModel[State::reordering].collectAsState(false)
    val saveSuccess by viewModel[State::saveSuccess].collectAsState(false)

    LaunchedEffect(tripId) {
        viewModel.getTrip(tripId)
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            navigateUp()
        }
    }

    if(start != null && date != null && itinerary.isNotEmpty() && name.isNotEmpty()) {
        Scaffold(
            topBar = {
                if (!reordering) {
                    TopBar(
                        title = "Edit Trip",
                        onBackArrow = { viewModel.saveTrip() },
                        actions = {
                            IconButton(onClick = { viewModel.toggleReordering() }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Reorder"
                                )
                            }
                        }
                    )
                }
            },
            floatingActionButton = {
                if (reordering) {
                    FloatingActionButton(onClick = { viewModel.toggleReordering() }) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = "Done")
                    }
                }
            },
        ) {
            when (reordering) {
                true -> {
                    ReorderingScreen(
                        start = start!!,
                        itinerary = itinerary,
                        updateTripOrder = viewModel::updateTripOrder,
                        padding = it
                    )
                }

                false -> {
                    CreateScreen(
                        name = name,
                        date = date,
                        start = start,
                        itinerary = itinerary,
                        padding = it,
                        onNameChange = viewModel::onNameChange,
                        onDateSelected = viewModel::onDateSelected,
                        onAddPlace = viewModel::onAddPlace
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReorderingScreen(
    start: Place,
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
        PlaceCard(start)
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
                                    .size(34.dp)
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
package kmp.android.trip.screens.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import kmp.android.shared.R
import kmp.android.shared.core.util.get
import kmp.android.shared.extension.showIn
import kmp.android.shared.navigation.composableDestination
import kmp.android.shared.ui.loading.FullScreenLoading
import kmp.android.shared.ui.loading.OverlayLoading
import kmp.android.trip.navigation.TripGraph
import kmp.android.shared.ui.place.lists.InactivePlaceList
import kmp.android.shared.ui.topbar.TopBar
import kmp.android.shared.ui.trip.DeleteDialog
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import org.koin.androidx.compose.getViewModel
import kmp.android.trip.screens.detail.DetailViewModel.ViewState as State

fun NavController.navigateToDetailScreen(tripId: Long) {
    navigate(TripGraph.Detail(tripId))
}

internal fun NavGraphBuilder.detailScreenRoute(navigateUp: () -> Unit, navigateToEdit: (Long) -> Unit){
    composableDestination(
        destination = TripGraph.Detail
    ) { navBackStackEntry ->
        val args = TripGraph.Detail.Args(navBackStackEntry.arguments)
        DetailRoute(
            tripId = args.tripId,
            navigateUp = navigateUp,
            navigateToEdit = navigateToEdit
        )
    }
}

@Composable
internal fun DetailRoute(
    tripId: Long,
    navigateUp: () -> Unit,
    navigateToEdit: (Long) -> Unit = {},
    viewModel: DetailViewModel = getViewModel(),
) {
    val snackHost = remember { SnackbarHostState() }
    val trip by viewModel[State::trip].collectAsState(null)
    val loading by viewModel[State::loading].collectAsState(false)
    val optimisingLoading by viewModel[State::optimisingLoading].collectAsState(false)

    val scrollState = rememberLazyListState()
    val isFloatingButtonExpanded = remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset <= 0 } }

    val context = LocalContext.current

    viewModel.errorFlow showIn snackHost

    var showDialog by remember { mutableStateOf(false) }

    if(showDialog) {
        DeleteDialog(
            onConfirm = { showDialog = false; viewModel.delete(); navigateUp() },
            onDismiss = { showDialog = false },
        )
    }

    LaunchedEffect(tripId) {
        viewModel.getTrip(tripId)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackHost) },
        topBar = {
            TopBar(
                title = trip?.name ?: stringResource(id = R.string.trip_detail_label),
                onBackArrow = navigateUp,
            ) {
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(id = R.string.delete),
                    )
                }
                IconButton(onClick = { navigateToEdit(tripId) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(id = R.string.edit),
                    )
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = stringResource(id = R.string.optimize),
                    )
                },
                text = { Text(stringResource(id = R.string.optimize)) },
                onClick = { viewModel.optimise() },
                expanded = isFloatingButtonExpanded.value,
            )
        },
    ) {
        if(loading){
            FullScreenLoading(text = stringResource(id = R.string.trip_loading))
        } else {
            trip?.let { trip ->
                DetailScreen(
                    trip = trip,
                    onPlaceClick = { place ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(place.googleMapsUri))
                        context.startActivity(intent)
                    },
                    scrollState = scrollState,
                    padding = it
                )
            }
        }
    }
    if(optimisingLoading){
        OverlayLoading(text = stringResource(id = R.string.trip_optimizing))
    }
}

@Composable
internal fun DetailScreen(
    trip: Trip,
    onPlaceClick: (Place) -> Unit,
    scrollState: LazyListState,
    padding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 8.dp)
    ) {
        InactivePlaceList(trip = trip, onPlaceClick = onPlaceClick, scrollState = scrollState)
    }
}
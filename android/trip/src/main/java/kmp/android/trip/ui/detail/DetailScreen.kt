package kmp.android.trip.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import kmp.android.shared.core.util.get
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.ui.create.PlaceCard
import kmp.android.trip.ui.home.PlaceCardListWithDistancesAndCurrent
import kmp.shared.domain.model.Location
import kmp.shared.domain.model.Place
import kmp.shared.domain.model.Trip
import org.koin.androidx.compose.getViewModel
import kmp.android.trip.ui.detail.DetailViewModel.ViewState as State

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

    val context = LocalContext.current

    LaunchedEffect(tripId) {
        viewModel.getTrip(tripId)
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackHost) },
        topBar = {
            TopBar(
                title = trip?.name?: "",
                onBackArrow = navigateUp
            ) {
                IconButton(onClick = { navigateToEdit(tripId) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = ""
                    )
                }
            }
        },
    ) {
        trip?.let { trip ->
            DetailScreen(
                trip = trip,
                onPlaceClick = { place ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(place.googleMapsUri))
                    context.startActivity(intent)
                },
                padding = it
            )
        }
    }
}

@Composable
internal fun DetailScreen(
    trip: Trip,
    onPlaceClick: (Place) -> Unit,
    padding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 8.dp)
    ) {
        Text(text = "${trip.date.dayOfMonth}.${trip.date.monthNumber}.${trip.date.year} ")

        PlaceCardListWithDistancesAndCurrent(trip = trip, null, { _, _ -> 0 }, onPlaceClick = onPlaceClick)
    }
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
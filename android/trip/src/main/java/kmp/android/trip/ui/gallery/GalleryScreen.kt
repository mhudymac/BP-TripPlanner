package kmp.android.trip.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import kmp.android.shared.core.ui.util.rememberCameraManager
import kmp.android.shared.core.ui.util.rememberCameraPermissionRequest
import kmp.android.shared.core.ui.util.rememberGalleryManager
import kmp.android.shared.core.ui.util.rememberGalleryPermissionRequest
import kmp.android.shared.core.util.get
import kmp.android.shared.navigation.composableDestination
import kmp.android.trip.navigation.TripGraph
import kmp.android.trip.ui.detail.TopBar
import kmp.shared.domain.model.Photo
import kmp.shared.domain.model.Trip
import kmp.shared.system.Log
import org.koin.androidx.compose.getViewModel
import kmp.android.trip.ui.gallery.GalleryViewModel.ViewState as State

fun NavController.navigateToGalleryScreen(tripId: Long) {
    navigate(TripGraph.Gallery(tripId))
}

internal fun NavGraphBuilder.galleryScreenRoute(navigateUp: () -> Unit){
    composableDestination(
        destination = TripGraph.Gallery
    ) { navBackStackEntry ->
        val args = TripGraph.Gallery.Args(navBackStackEntry.arguments)
        GalleryRoute(
            tripId = args.tripId,
            navigateUp = navigateUp,
        )
    }
}

@Composable
internal fun GalleryRoute(
    tripId: Long,
    navigateUp: () -> Unit,
    viewModel: GalleryViewModel = getViewModel(),
) {
    val snackHost = remember { SnackbarHostState() }
    val trip by viewModel[State::trip].collectAsState(null)
    val loading by viewModel[State::loading].collectAsState(false)
    val photos by viewModel[State::photos].collectAsState(emptyList())
    val editing by viewModel[State::editing].collectAsState(false)

    val galleryPermissionHandler = rememberGalleryPermissionRequest()
    val cameraPermissionGranted by galleryPermissionHandler.granted

    val galleryManager = rememberGalleryManager {
        viewModel.addUserPhoto(it.toString())
    }

    LaunchedEffect(tripId) {
        viewModel.getAll(tripId)
    }

    LaunchedEffect(cameraPermissionGranted) {
        if(cameraPermissionGranted) {
            galleryManager.launch()
        }
    }



    Scaffold(
        topBar = {
            TopBar(
                title = trip?.name ?: "Gallery",
                onBackArrow = navigateUp,
                showBackArrow = !editing
            ) {
                IconButton(onClick = { viewModel.delete() ;navigateUp() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete icon"
                    )
                }
                IconButton(onClick = { viewModel.editing = !editing}) {
                    Icon(
                        imageVector = if(editing) Icons.Filled.Done else Icons.Outlined.Edit,
                        contentDescription = "Edit icon"
                    )
                }
            }
        }
    ) {
        if(loading){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (trip != null && photos.isNotEmpty()) {
                GalleryScreen(
                    trip = trip!!,
                    photos = photos,
                    editing = editing,
                    onAddPhoto = { placeId ->
                        viewModel.currentPlaceId = placeId
                        if (cameraPermissionGranted) {
                            galleryManager.launch()
                        } else {
                            galleryPermissionHandler.requestPermission()
                        }
                    },
                    padding = it
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No saved images with this trip", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
private fun GalleryScreen(
    trip: Trip,
    photos: List<Photo>,
    editing: Boolean,
    onAddPhoto: (String) -> Unit,
    padding: PaddingValues  = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = Modifier
            .padding(padding),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(trip.itinerary) { place ->
            val placePhotos = photos.filter { it.placeId == place.id }

            if(placePhotos.isNotEmpty() || editing) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(start = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(placePhotos) { photo ->
                            SubcomposeAsyncImage(
                                model = photo.photoUri,
                                contentDescription = "Place Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(170.dp)
                                    .clip(MaterialTheme.shapes.medium),
                                loading = {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            )
                        }
                        if (editing) {
                            item {
                                Card(
                                    onClick = { onAddPhoto(place.id) },
                                    modifier = Modifier.size(170.dp),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Add,
                                            contentDescription = "Add Photo",
                                            modifier = Modifier.size(100.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
package kmp.android.trip.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import kmp.shared.domain.model.Place
import kmp.android.shared.R


@Composable
internal fun ActivePlaceCard(
    place: Place,
    onClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    images: List<Uri> = emptyList()
){
    PlaceCard(
        place = place,
        onClick = onClick,
        height = 200,
        colors = CardDefaults.elevatedCardColors().copy(
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onCameraClick) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = stringResource(id = R.string.take_picture),
                )
            }
            PhotoRow(images = images)
        }
    }
}

@Composable
internal fun PhotoRow(images: List<Uri>){
    LazyRow {
        items(images) { imageUri ->
            SubcomposeAsyncImage(
                model = imageUri,
                contentDescription = stringResource(R.string.place_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .padding(8.dp)
                    .clip(MaterialTheme.shapes.extraSmall),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
            )
        }
    }
}

@Composable
internal fun PlaceCard(
    place: Place,
    onClick: () -> Unit = {},
    onDeleteClick: (() -> Unit)? = null,
    colors: CardColors = CardDefaults.elevatedCardColors(),
    height: Int = 120,
    trailingIcon: @Composable () -> Unit = {},
    expandedContent: @Composable () -> Unit = {},
) {
    Box {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp)
                .padding(vertical = 8.dp),
            colors = colors,
            shape = MaterialTheme.shapes.large,
            onClick = onClick,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SubcomposeAsyncImage(
                        model = place.photoUri,
                        contentDescription = stringResource(id = R.string.place_image),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(112.dp)
                            .padding(8.dp)
                            .clip(MaterialTheme.shapes.large),
                        loading = {
                            FullScreenLoading()
                        },
                        error = {
                            Image(
                                painter = painterResource(id = R.drawable.placeholder_view_vector),
                                contentDescription = stringResource(id = R.string.place_image),
                                contentScale = ContentScale.Crop,
                            )
                        },
                    )

                    PlaceInfo(place = place, modifier = Modifier
                        .weight(1f)
                        .padding(8.dp))

                    trailingIcon()
                }
                expandedContent()
            }
        }

        if(onDeleteClick != null) {
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.delete),
                    modifier = Modifier.size(26.dp),
                    tint = Color.Red,
                )
            }
        }
    }
}

@Composable
internal fun PlaceInfo(place: Place, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = place.name,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = place.formattedAddress,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
internal fun EmptyPlaceCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ElevatedCard(
        modifier = modifier
            .height(120.dp)
            .padding(vertical = 8.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@Composable
internal fun AddPlaceCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EmptyPlaceCard(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(id = R.string.add_place),
        )
        Text(text = stringResource(id = R.string.add_place))
    }
}
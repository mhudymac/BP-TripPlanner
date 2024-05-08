package kmp.android.shared.ui.helpers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kmp.android.shared.ui.place.EmptyPlaceCard

@Composable
fun OutlinedTextFieldLikeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
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
fun ComponentWithLabel(
    label: String,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(top = 16.dp),
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(text = "$label:", modifier = Modifier.padding(padding))
        content()
    }
}

@Composable
fun TwoCardButtons(
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
    contentLeft: @Composable () -> Unit,
    contentRight: @Composable () -> Unit,
    modifier: Modifier = Modifier,
){
    Row(
        modifier = modifier.fillMaxWidth(),
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

package kmp.android.shared.ui.distance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kmp.android.shared.R

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
                text = "$distanceInMinutes " + stringResource(id = R.string.minutes),
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
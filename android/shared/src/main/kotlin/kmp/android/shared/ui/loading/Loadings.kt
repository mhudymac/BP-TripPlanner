package kmp.android.shared.ui.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun FullScreenLoading( modifier: Modifier = Modifier, text: String = "" ) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        if(text.isNotEmpty()) {
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun OverlayLoading( modifier: Modifier = Modifier, text: String = "" ) {
    Card(
        modifier = modifier.fillMaxSize(),
        colors = CardDefaults.cardColors().copy( containerColor = Color.Black.copy(alpha = 0.5f)),
    ) {
        FullScreenLoading(text = text)
    }
}

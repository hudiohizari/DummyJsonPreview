package id.my.hizari.dummyjsonpreview.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.hizari.dummyjsonpreview.ui.theme.DummyJsonPreviewTheme

/**
 * id.my.hizari.dummyjsonpreview.ui.components
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * Takes over the whole screen when there is nothing else to show: empty, error or coming soon.
 * Supplying an action turns it into a recoverable error.
 *
 * When real content is already on screen use [StateBanner] instead.
 */
@Composable
fun StateMessage(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    message: String,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(size = 40.dp),
            imageVector = icon,
            contentDescription = null,
            tint = iconTint
        )
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Button(
                modifier = Modifier.padding(top = 20.dp),
                onClick = onAction,
                content = { Text(text = actionLabel) }
            )
        }
    }
}

@Preview(name = "State empty", showBackground = true)
@Composable
private fun StateMessageEmptyPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        StateMessage(
            icon = Icons.Default.SearchOff,
            title = "No products found",
            message = "Try a different search term."
        )
    }
}

@Preview(name = "State error", showBackground = true)
@Composable
private fun StateMessageErrorPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        StateMessage(
            icon = Icons.Default.ErrorOutline,
            title = "Something went wrong",
            message = "Network unavailable",
            iconTint = MaterialTheme.colorScheme.error,
            actionLabel = "Retry",
            onAction = {}
        )
    }
}

@Preview(name = "State coming soon", showBackground = true)
@Composable
private fun StateMessageComingSoonPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        StateMessage(
            icon = Icons.Default.Schedule,
            title = "Categories",
            message = "This section is not available yet."
        )
    }
}

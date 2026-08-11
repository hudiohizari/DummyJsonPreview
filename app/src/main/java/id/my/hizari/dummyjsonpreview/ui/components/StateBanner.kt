package id.my.hizari.dummyjsonpreview.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
 * A single row message that sits above or below content that is still usable, so a failure does
 * not throw away what the user can already see. Supplying an action adds an inline retry.
 *
 * When there is nothing behind it use [StateMessage] instead.
 */
@Composable
fun StateBanner(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    message: String,
    iconTint: Color = MaterialTheme.colorScheme.error,
    containerColor: Color = Color.Transparent,
    messageColor: Color = MaterialTheme.colorScheme.error,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = containerColor)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(size = 20.dp),
            imageVector = icon,
            contentDescription = null,
            tint = iconTint
        )
        Text(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(start = 8.dp),
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = messageColor
        )
        if (actionLabel != null && onAction != null) {
            TextButton(
                onClick = onAction,
                content = { Text(text = actionLabel) }
            )
        }
    }
}

@Preview(name = "State banner", showBackground = true)
@Composable
private fun StateBannerPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        StateBanner(
            icon = Icons.Default.ErrorOutline,
            message = "Network unavailable",
            iconTint = MaterialTheme.colorScheme.onErrorContainer,
            containerColor = MaterialTheme.colorScheme.errorContainer,
            messageColor = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Preview(name = "State banner with retry", showBackground = true)
@Composable
private fun StateBannerActionPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        StateBanner(
            icon = Icons.Default.ErrorOutline,
            message = "Network unavailable",
            actionLabel = "Retry",
            onAction = {}
        )
    }
}

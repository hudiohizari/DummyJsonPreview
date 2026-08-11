package id.my.hizari.dummyjsonpreview.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import id.my.hizari.dummyjsonpreview.R
import id.my.hizari.dummyjsonpreview.ui.theme.DummyJsonPreviewTheme

/**
 * id.my.hizari.dummyjsonpreview.ui.components
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * Reports the outcome of a write and nothing else. Every write in the app confirms the same way,
 * so dismissing is the only choice on offer.
 */
@Composable
fun MessageDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    confirmLabel: String = stringResource(id = R.string.action_ok)
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onDismiss, content = { Text(text = confirmLabel) })
        }
    )
}

@Preview(name = "Message dialog", showBackground = true)
@Composable
private fun MessageDialogPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        MessageDialog(
            title = "Product saved",
            message = "Essence Mascara Lash Princess was saved.",
            onDismiss = {}
        )
    }
}

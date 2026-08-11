package id.my.hizari.dummyjsonpreview.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
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
 * The toolbar every screen with one uses. The back arrow appears only for screens that were pushed
 * onto a tab, so the tab roots do not have to pass anything to leave it out.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(
                    onClick = onNavigateBack,
                    content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.action_back)
                        )
                    }
                )
            }
        },
        actions = actions
    )
}

@Preview(name = "Top bar", showBackground = true)
@Composable
private fun AppTopBarPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        AppTopBar(title = "Add product")
    }
}

@Preview(name = "Top bar with back", showBackground = true)
@Composable
private fun AppTopBarBackPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        AppTopBar(title = "Essence Mascara Lash Princess", onNavigateBack = {})
    }
}

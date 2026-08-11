package id.my.hizari.dummyjsonpreview.feature.comingsoon

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import id.my.hizari.dummyjsonpreview.R
import id.my.hizari.dummyjsonpreview.ui.components.StateMessage
import id.my.hizari.dummyjsonpreview.ui.theme.DummyJsonPreviewTheme

/**
 * id.my.hizari.dummyjsonpreview.feature.comingsoon
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * One composable serving both placeholder tabs, parameterised by title.
 */
@Composable
fun ComingSoonScreen(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int
) {
    StateMessage(
        modifier = modifier,
        title = stringResource(id = titleRes),
        message = stringResource(id = R.string.coming_soon_message),
        icon = Icons.Default.Schedule
    )
}

@Preview(name = "Coming soon", showBackground = true)
@Composable
private fun ComingSoonScreenPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ComingSoonScreen(titleRes = R.string.nav_categories)
    }
}

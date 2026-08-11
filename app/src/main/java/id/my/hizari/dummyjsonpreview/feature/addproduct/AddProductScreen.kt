package id.my.hizari.dummyjsonpreview.feature.addproduct

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import id.my.hizari.dummyjsonpreview.R
import id.my.hizari.dummyjsonpreview.ui.components.PlaceholderContent
import id.my.hizari.dummyjsonpreview.ui.theme.DummyJsonPreviewTheme

/**
 * id.my.hizari.dummyjsonpreview.feature.addproduct
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@Composable
fun AddProductScreen(modifier: Modifier = Modifier) {
    PlaceholderContent(
        title = stringResource(id = R.string.title_add_product),
        message = stringResource(id = R.string.placeholder_add_product),
        icon = Icons.Default.AddBox,
        modifier = modifier
    )
}

@Preview(name = "Add product", showBackground = true)
@Composable
private fun AddProductScreenPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        AddProductScreen()
    }
}

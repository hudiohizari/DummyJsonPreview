package id.my.hizari.dummyjsonpreview.feature.product.edit

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import id.my.hizari.dummyjsonpreview.R
import id.my.hizari.dummyjsonpreview.ui.components.PlaceholderContent
import id.my.hizari.dummyjsonpreview.ui.theme.DummyJsonPreviewTheme

/**
 * id.my.hizari.dummyjsonpreview.feature.product.edit
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@Composable
fun ProductEditScreen(
    productId: Int,
    modifier: Modifier = Modifier
) {
    PlaceholderContent(
        title = stringResource(id = R.string.title_product_edit, productId),
        message = stringResource(id = R.string.placeholder_edit),
        icon = Icons.Default.Edit,
        modifier = modifier
    )
}

@Preview(name = "Product edit", showBackground = true)
@Composable
private fun ProductEditScreenPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductEditScreen(productId = 1)
    }
}

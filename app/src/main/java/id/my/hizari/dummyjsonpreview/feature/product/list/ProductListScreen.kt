package id.my.hizari.dummyjsonpreview.feature.product.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.hizari.dummyjsonpreview.R
import id.my.hizari.dummyjsonpreview.ui.theme.DummyJsonPreviewTheme

/**
 * id.my.hizari.dummyjsonpreview.feature.product.list
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@Composable
fun ProductListScreen(
    modifier: Modifier = Modifier,
    onProductClick: (Int) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.title_products))
        Button(
            modifier = Modifier.padding(top = 12.dp),
            onClick = { onProductClick(1) },
            content = { Text(text = stringResource(id = R.string.action_open_sample_product)) }
        )
    }
}

@Preview(name = "Product list", showBackground = true)
@Composable
private fun ProductListScreenPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductListScreen(onProductClick = {})
    }
}

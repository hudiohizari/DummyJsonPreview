package id.my.hizari.dummyjsonpreview.feature.product.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import id.my.hizari.dummyjsonpreview.domain.product.model.Product
import id.my.hizari.dummyjsonpreview.ui.theme.DummyJsonPreviewTheme
import id.my.hizari.dummyjsonpreview.util.toPriceLabel

/**
 * id.my.hizari.dummyjsonpreview.feature.product.list
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@Composable
fun ProductListItem(
    modifier: Modifier = Modifier,
    product: Product,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(size = 64.dp)
                .clip(shape = RoundedCornerShape(size = 8.dp)),
            model = product.thumbnail,
            contentDescription = product.title,
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = product.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = product.category.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = product.price.toPriceLabel(),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(name = "Product row", showBackground = true)
@Composable
private fun ProductListItemPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductListItem(
            product = Product(
                id = 1,
                title = "Essence Mascara Lash Princess",
                description = null,
                category = "beauty",
                price = 9.99,
                discountPercentage = 7.17,
                rating = 4.94,
                stock = 5,
                brand = "Essence",
                sku = null,
                weight = null,
                dimensions = null,
                warrantyInformation = null,
                shippingInformation = null,
                availabilityStatus = null,
                returnPolicy = null,
                minimumOrderQuantity = null,
                meta = null,
                thumbnail = null
            ),
            onClick = {}
        )
    }
}

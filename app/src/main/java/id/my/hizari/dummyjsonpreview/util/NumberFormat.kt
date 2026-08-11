package id.my.hizari.dummyjsonpreview.util

import java.util.Locale

/**
 * id.my.hizari.dummyjsonpreview.util
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/** DummyJSON prices are plain USD amounts, so a fixed two-decimal label is enough. */
fun Double.toPriceLabel(): String = String.format(Locale.US, "$%.2f", this)

/** Discounts arrive as values such as 7.17, which reads better rounded to a whole percent. */
fun Double.toPercentLabel(): String = String.format(Locale.US, "%.0f", this)

/** Drops the trailing zero so 2.0 reads as "2" while 12.5 keeps its fraction. */
fun Double.toDecimalLabel(): String = if (this % 1.0 == 0.0) {
    String.format(Locale.US, "%.0f", this)
} else {
    String.format(Locale.US, "%.1f", this)
}

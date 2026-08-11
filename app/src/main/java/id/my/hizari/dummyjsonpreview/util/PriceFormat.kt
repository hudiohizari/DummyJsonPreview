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

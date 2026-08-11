package id.my.hizari.dummyjsonpreview.feature.product.form

/**
 * id.my.hizari.dummyjsonpreview.feature.product.form
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

private const val DECIMAL_SEPARATOR = '.'

/**
 * Keyboard type is only a hint to the soft keyboard, so a paste, a hardware keyboard or a keyboard
 * that offers more still puts anything it likes in the field. These decide what is actually kept.
 */
fun String.filteredAsDecimal(): String = buildString(builderAction = {
    var hasSeparator = false
    this@filteredAsDecimal.forEach(action = { character ->
        when {
            character.isDigit() -> append(character)
            // A comma is what the keyboard offers in locales that use one, so it is the same key.
            !hasSeparator && (character == DECIMAL_SEPARATOR || character == ',') -> {
                hasSeparator = true
                append(DECIMAL_SEPARATOR)
            }
        }
    })
})

fun String.filteredAsInteger(): String = filter(predicate = Char::isDigit)

/**
 * Parses a typed amount, accepting the comma separator and refusing a value too large to hold.
 * Without the finite check an overflowing entry becomes an infinity, which passes a greater than
 * zero test and is then rejected by the JSON serialiser instead.
 */
fun String.toFormDouble(): Double? = trim()
    .replace(oldChar = ',', newChar = DECIMAL_SEPARATOR)
    .toDoubleOrNull()
    ?.takeIf { it.isFinite() }

fun String.toFormInt(): Int? = trim().toIntOrNull()

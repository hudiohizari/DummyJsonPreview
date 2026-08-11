package id.my.hizari.dummyjsonpreview.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * id.my.hizari.dummyjsonpreview.ui.theme
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/* Every surface and on-surface colour is set explicitly. Leaving them to the Material defaults is
   what produced washed out body text and low contrast text fields. */
private val LightColors = lightColorScheme(
    primary = BlueLight,
    onPrimary = BlueLightOn,
    primaryContainer = BlueLightContainer,
    onPrimaryContainer = BlueLightOnContainer,
    secondary = SlateLight,
    onSecondary = BlueLightOn,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceLightVariant,
    onSurfaceVariant = OnSurfaceLightVariant,
    surfaceContainerLowest = ContainerLightLowest,
    surfaceContainerLow = ContainerLightLow,
    surfaceContainer = ContainerLight,
    surfaceContainerHigh = ContainerLightHigh,
    surfaceContainerHighest = ContainerLightHighest,
    surfaceTint = BlueLight,
    outline = OutlineLight,
    error = ErrorLight,
    onError = ErrorLightOn
)

private val DarkColors = darkColorScheme(
    primary = BlueDark,
    onPrimary = BlueDarkOn,
    primaryContainer = BlueDarkContainer,
    onPrimaryContainer = BlueDarkOnContainer,
    secondary = SlateDark,
    onSecondary = BlueDarkOn,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceDarkVariant,
    onSurfaceVariant = OnSurfaceDarkVariant,
    surfaceContainerLowest = ContainerDarkLowest,
    surfaceContainerLow = ContainerDarkLow,
    surfaceContainer = ContainerDark,
    surfaceContainerHigh = ContainerDarkHigh,
    surfaceContainerHighest = ContainerDarkHighest,
    surfaceTint = BlueDark,
    outline = OutlineDark,
    error = ErrorDark,
    onError = ErrorDarkOn
)

@Composable
fun DummyJsonPreviewTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Off by default: dynamic colour derives the palette from the device wallpaper, so screenshots
    // and previews would not match what a reviewer sees.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context = context)
            } else {
                dynamicLightColorScheme(context = context)
            }
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        // Wrapped in a Surface so LocalContentColor resolves to onBackground. Without it Material
        // leaves the default content colour black, which is invisible in dark mode for any text
        // that does not set a colour of its own.
        content = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                content = content
            )
        }
    )
}

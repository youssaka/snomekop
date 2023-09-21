package theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.loyou.snomekop.ui.theme.ColorPrimary
import com.loyou.snomekop.ui.theme.DarkGray
import com.loyou.snomekop.ui.theme.MediumGray
import com.loyou.snomekop.ui.theme.TextWhite

private val DarkColorPalette = darkColorScheme(
    primary = ColorPrimary,
    background = DarkGray,
    onBackground = TextWhite,
    onPrimary = DarkGray
)

private val LightColorPalette = lightColorScheme(
    primary = ColorPrimary,
    background = Color.White,
    onBackground = MediumGray,
    onPrimary = DarkGray
)

@Composable
fun SnomekopTheme(darkTheme: Boolean = false, content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
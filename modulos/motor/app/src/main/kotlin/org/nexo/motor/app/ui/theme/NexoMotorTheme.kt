package org.nexo.motor.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Cor semente decidida em decisions/0035 -- laranja e azul royal.
private val SeedOrange = Color(0xFFFF6D1F)
private val SeedRoyalBlue = Color(0xFF2D5FE0)

private val LightColorScheme = lightColorScheme(
    primary = SeedOrange,
    secondary = SeedRoyalBlue,
)

private val DarkColorScheme = darkColorScheme(
    primary = SeedOrange,
    secondary = SeedRoyalBlue,
)

/**
 * Casca visual única do motor (decisions/0035): Material Design 3, cor dinâmica quando o
 * aparelho suporta (Android 12/API 31 em diante), semente laranja/azul royal nos demais; tema
 * claro/escuro seguindo o que a pessoa já configurou no sistema. Tipografia e forma seguem o
 * padrão do próprio Material 3, sem ajuste — decisions/0035 não pede nenhum.
 */
@Composable
fun NexoMotorTheme(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()
    val context = LocalContext.current
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme ->
            dynamicDarkColorScheme(context)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !darkTheme ->
            dynamicLightColorScheme(context)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}

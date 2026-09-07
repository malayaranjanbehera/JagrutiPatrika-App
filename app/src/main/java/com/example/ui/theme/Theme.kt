package com.example.ui.theme

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

private val ElegantDarkColorScheme = darkColorScheme(
  primary = LavenderPrimary,                 // #D0BCFF
  onPrimary = OnLavenderPrimary,             // #381E72
  primaryContainer = DeepVioletContainer,     // #4F378B
  onPrimaryContainer = OnVioletContainer,     // #EADDFF
  secondary = Color(0xFFCCC2DC),
  onSecondary = Color(0xFF332D41),
  secondaryContainer = Color(0xFF4A4458),
  onSecondaryContainer = Color(0xFFE8DEF8),
  tertiary = GoldPrimary,                    // #FFD56B
  onTertiary = Color(0xFF3E2E04),
  tertiaryContainer = GoldContainer,         // #4A3B18
  onTertiaryContainer = OnGoldContainer,     // #FFE599
  background = ObsidianBg,                   // #1C1B1F
  onBackground = TextDarkHighContrast,       // #E6E1E5
  surface = DarkSurface,                     // #2B2930
  onSurface = TextDarkHighContrast,          // #E6E1E5
  surfaceVariant = DarkSurfaceElevated,       // #332D41
  onSurfaceVariant = TextDarkMuted,          // #CAC4D0
  outline = DarkBorder,                      // #49454F
  outlineVariant = DarkBorderSubtle          // #36343B
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to requested Elegant Dark design theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = ElegantDarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}


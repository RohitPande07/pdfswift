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

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryBlueDark,
  onPrimary = Color(0xFF002F6C),
  primaryContainer = PrimaryBlueContainerDark,
  onPrimaryContainer = Color(0xFFD6E3FF),
  secondary = SecondaryTealDark,
  onSecondary = Color(0xFF003734),
  secondaryContainer = SecondaryTealContainerDark,
  onSecondaryContainer = Color(0xFF99F2EB),
  tertiary = AccentRedDark,
  onTertiary = Color(0xFF4C0015),
  tertiaryContainer = AccentRedContainerDark,
  onTertiaryContainer = Color(0xFFFFD9DF),
  background = BgDark,
  onBackground = TextPrimaryDark,
  surface = SurfaceDark,
  onSurface = TextPrimaryDark,
  surfaceVariant = SurfaceVariantDark,
  onSurfaceVariant = TextSecondaryDark,
  outline = BorderDark
)

private val LightColorScheme = lightColorScheme(
  primary = PrimaryBlue,
  onPrimary = Color.White,
  primaryContainer = PrimaryBlueContainer,
  onPrimaryContainer = Color(0xFF001B3F),
  secondary = SecondaryTeal,
  onSecondary = Color.White,
  secondaryContainer = SecondaryTealContainer,
  onSecondaryContainer = Color(0xFF00201E),
  tertiary = AccentRed,
  onTertiary = Color.White,
  tertiaryContainer = AccentRedContainer,
  onTertiaryContainer = Color(0xFF400010),
  background = BgLight,
  onBackground = TextPrimaryLight,
  surface = SurfaceLight,
  onSurface = TextPrimaryLight,
  surfaceVariant = SurfaceVariantLight,
  onSurfaceVariant = TextSecondaryLight,
  outline = BorderLight
)

@Composable
fun PDFSwiftTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent branding
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

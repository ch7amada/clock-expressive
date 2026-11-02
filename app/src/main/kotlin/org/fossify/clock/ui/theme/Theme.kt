package org.fossify.clock.ui.theme

import android.content.SharedPreferences
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.fossify.clock.extensions.config
import org.fossify.clock.helpers.Config
import org.fossify.clock.helpers.THEME_MODE_DARK
import org.fossify.clock.helpers.THEME_MODE_LIGHT

data class ThemeSettings(val mode: Int, val dynamicColor: Boolean, val pureBlack: Boolean)

@Composable
fun rememberThemeSettings(): ThemeSettings {
    val context = LocalContext.current
    // Config is recreated on every `context.config` access, so remember a single instance
    // to keep the DisposableEffect key stable across recompositions.
    val config = remember(context) { context.config }
    val settings = remember { mutableStateOf(readThemeSettings(config)) }
    DisposableEffect(config) {
        // Hold a strong reference: SharedPreferences keeps listeners weakly.
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            settings.value = readThemeSettings(config)
        }
        config.registerPrefsListener(listener)
        onDispose { config.unregisterPrefsListener(listener) }
    }
    return settings.value
}

private fun readThemeSettings(config: Config): ThemeSettings = ThemeSettings(
    mode = config.themeMode,
    dynamicColor = config.useDynamicColor,
    pureBlack = config.usePureBlack,
)

@Composable
private fun ThemeSettings.isDark(): Boolean = when (mode) {
    THEME_MODE_LIGHT -> false
    THEME_MODE_DARK -> true
    else -> isSystemInDarkTheme()
}

@Composable
fun isAppInDarkTheme(): Boolean {
    val settings = rememberThemeSettings()
    return settings.isDark()
}

@Composable
fun ClockTheme(
    settings: ThemeSettings = rememberThemeSettings(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val darkTheme = settings.isDark()

    val useDynamicColor = settings.dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = remember(darkTheme, useDynamicColor, settings.pureBlack) {
        val base = when {
            darkTheme && useDynamicColor -> dynamicDarkColorScheme(context)
            darkTheme -> DarkColorScheme
            useDynamicColor -> dynamicLightColorScheme(context)
            else -> LightColorScheme
        }
        if (darkTheme && settings.pureBlack) {
            base.toPureBlack()
        } else {
            base
        }
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        shapes = ClockShapes,
        typography = ClockTypography,
        content = content,
    )
}

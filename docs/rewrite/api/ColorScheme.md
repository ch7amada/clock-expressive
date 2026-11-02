### ColorScheme.kt (material3 1.5.0-beta02) — color scheme builders
- `public fun lightColorScheme( ...)`
- `public fun darkColorScheme( ...)`
- `public fun expressiveLightColorScheme(): ColorScheme = ...)`
- `public fun lightColorScheme( ...)`
- `public fun lightColorScheme( ...)`
- `public fun darkColorScheme( ...)`
- `public fun darkColorScheme( ...)`
- `public fun dynamicLightColorScheme(context: Context): ColorScheme` (API 31+, androidMain)
- `public fun dynamicDarkColorScheme(context: Context): ColorScheme` (API 31+, androidMain)
- ColorScheme has `.copy(primary = ..., surfaceContainerLowest = ..., ...)` with every role as a named parameter.

lightColorScheme/darkColorScheme parameter names (all Color, all defaulted):
primary onPrimary primaryContainer onPrimaryContainer inversePrimary secondary onSecondary secondaryContainer onSecondaryContainer tertiary onTertiary tertiaryContainer onTertiaryContainer background onBackground surface onSurface surfaceVariant onSurfaceVariant surfaceTint inverseSurface inverseOnSurface error onError errorContainer onErrorContainer outline outlineVariant scrim surfaceBright surfaceContainer surfaceContainerHigh surfaceContainerHighest surfaceContainerLow surfaceContainerLowest surfaceDim primaryFixed primaryFixedDim onPrimaryFixed onPrimaryFixedVariant secondaryFixed secondaryFixedDim onSecondaryFixed onSecondaryFixedVariant tertiaryFixed tertiaryFixedDim onTertiaryFixed onTertiaryFixedVariant 

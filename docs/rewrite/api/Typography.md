### Typography.kt (material3 1.5.0-beta02)
- `Typography(fontFamily: FontFamily, displayLarge: TextStyle? = null, ... all 15 styles ..., displayLargeEmphasized: TextStyle? = null, ... all 15 *Emphasized styles)` — applies fontFamily to every style; null = default style
- Also the classic `Typography(displayLarge = TextStyle, ...)` constructor and `.copy(...)` with all 30 styles (15 base + 15 `*Emphasized`).
- Access: `MaterialTheme.typography.displayLargeEmphasized`, `headlineSmallEmphasized`, `titleMediumEmphasized`, `labelLargeEmphasized`, ...

### Compose UI variable fonts (ui-text 1.12)
- `Font(resId: Int, weight: FontWeight = FontWeight.Normal, style: FontStyle = FontStyle.Normal, variationSettings: FontVariation.Settings = FontVariation.Settings(weight, style))`
- `FontVariation.Settings(vararg settings: FontVariation.Setting)`; `FontVariation.weight(Int)`, `FontVariation.width(Float)`, `FontVariation.slant(Float)`, `FontVariation.opticalSizing(TextUnit)`, custom axis `FontVariation.Setting("ROND", 100f)`
- `FontFamily(vararg fonts: Font)`; `TextStyle(fontFeatureSettings = "tnum")` for tabular digits

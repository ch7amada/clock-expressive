# Compose animation + graphics cheat sheet (stable APIs — exact imports)

## Value animation
```kotlin
import androidx.compose.animation.core.animateFloatAsState      // val v by animateFloatAsState(target, animationSpec = spec, label = "x")
import androidx.compose.animation.animateColorAsState            // val c by animateColorAsState(target, animationSpec = spec)
import androidx.compose.animation.core.Animatable                // val a = remember { Animatable(0f) }; LaunchedEffect(k) { a.animateTo(1f, spec) }
import androidx.compose.runtime.getValue                         // needed for `by` delegation
```
Specs from the theme: `MaterialTheme.motionScheme.defaultSpatialSpec<Float>()`, `fastSpatialSpec<Float>()`,
`defaultEffectsSpec<Float>()`, `slowSpatialSpec<Float>()` — **the type parameter is required** (`<Float>`, `<Color>`, `<Dp>`),
otherwise "Cannot infer type for type parameter 'T'". Read them inside a @Composable, not inside a lambda.

## Infinite animation
```kotlin
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat              // extension on InfiniteTransition — must be imported explicitly
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode

val t = rememberInfiniteTransition(label = "spin")
val angle by t.animateFloat(
    initialValue = 0f, targetValue = 360f,
    animationSpec = infiniteRepeatable(tween(durationMillis = 20_000, easing = LinearEasing), RepeatMode.Restart),
    label = "angle",
)
```
(A constant-speed rotation is the one place a linear tween is correct.)

## AnimatedContent / AnimatedVisibility
```kotlin
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.togetherWith                    // infix: enter togetherWith exit -> ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically

AnimatedContent(
    targetState = playing,
    transitionSpec = { (fadeIn(spec) + scaleIn(initialScale = 0.6f)) togetherWith (fadeOut() + scaleOut(targetScale = 0.6f)) },
    label = "icon",
) { isPlaying -> Icon(...) }
```
There is **no** `fadeInAndScaleIn` / `fadeOutAndScaleOut` — combine with `+`.

## Modifiers
- Prefer `Modifier.graphicsLayer { scaleX = s; scaleY = s; rotationZ = a }` (`androidx.compose.ui.graphics.graphicsLayer`)
  over `Modifier.scale()` / `rotate()` when the value changes every frame.
- `Modifier.scale(s)` is `androidx.compose.ui.draw.scale`; `Modifier.rotate(deg)` is `androidx.compose.ui.draw.rotate`.
- `Modifier.composed { }` (`androidx.compose.ui.composed`) is discouraged. For a modifier that needs remember/animation,
  write a `@Composable fun Modifier.foo(): Modifier { val x by animate...; return this.graphicsLayer { ... } }`.
- Press state: `val src = remember { MutableInteractionSource() }; val pressed by src.collectIsPressedAsState()`
  (`androidx.compose.foundation.interaction.MutableInteractionSource`, `...interaction.collectIsPressedAsState`).

## CompositionLocals
- `androidx.compose.ui.platform.LocalHapticFeedback` → `.current.performHapticFeedback(HapticFeedbackType.Confirm)`
  (`androidx.compose.ui.hapticfeedback.HapticFeedbackType`: `Confirm`, `Reject`, `ToggleOn`, `ToggleOff`, `LongPress`,
  `TextHandleMove`, `ContextClick`, `SegmentTick`, `VirtualKey`, `KeyboardTap`, `GestureEnd`).
- `androidx.compose.material3.LocalContentColor`.
- `androidx.compose.ui.platform.LocalDensity`, `LocalContext`, `LocalLayoutDirection`.

## Shapes from RoundedPolygon / Morph (graphics-shapes + material3)
```kotlin
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape                         // RoundedPolygon.toShape(startAngle: Int = 0): Shape
import androidx.compose.material3.toPath                          // RoundedPolygon.toPath / Morph.toPath(progress, path, startAngle)
import androidx.graphics.shapes.Morph                            // Morph(start: RoundedPolygon, end: RoundedPolygon)
import androidx.compose.ui.graphics.Matrix                       // Compose Matrix (NOT android.graphics.Matrix)
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
```
Material shapes are normalized to a 0..1 box. To fill a `size`:
```kotlin
class MorphShape(private val morph: Morph, private val progress: Float) : Shape {
    private val matrix = Matrix()
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = morph.toPath(progress = progress)            // returns a Compose Path in 0..1 space
        matrix.reset(); matrix.scale(size.width, size.height)
        path.transform(matrix)
        return Outline.Generic(path)
    }
}
```
Always `remember(start, end) { Morph(start, end) }` — building a Morph is expensive.

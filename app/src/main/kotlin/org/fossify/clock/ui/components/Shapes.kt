package org.fossify.clock.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.toPath
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Size
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import org.fossify.clock.R
import org.fossify.clock.ui.theme.ClockTheme
import kotlin.math.roundToInt

/**
 * A [Shape] built from a [Morph] between two [RoundedPolygon]s.
 *
 * `MaterialShapes` polygons are normalized to the 0..1 unit square, so [createOutline]
 * scales the morphed path to the actual composable size.
 *
 * @param morph the morph between start and end polygons
 * @param progress interpolation progress, 0f = start polygon, 1f = end polygon
 * @param startAngle rotation of the polygon, in degrees
 */
class MorphPolygonShape(
    private val morph: Morph,
    private val progress: Float,
    private val startAngle: Int = 0,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = morph.toPath(progress, Path(), startAngle)
        val matrix = Matrix().apply { scale(size.width, size.height) }
        path.transform(matrix)
        return Outline.Generic(path)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MorphPolygonShape) return false
        return morph === other.morph && progress == other.progress && startAngle == other.startAngle
    }

    override fun hashCode(): Int {
        var result = System.identityHashCode(morph)
        result = 31 * result + progress.hashCode()
        result = 31 * result + startAngle
        return result
    }
}

/**
 * Remembers a [Morph] between two polygons so it is not recreated on every recomposition.
 */
@Composable
fun rememberMorph(start: RoundedPolygon, end: RoundedPolygon): Morph = remember(start, end) {
    Morph(start, end)
}

/**
 * Returns a [Shape] whose outline is morphed between [start] and [end], with the
 * animation progress driven by [atEnd] (0f = [start], 1f = [end]).
 */
@Composable
fun animatedMorphShape(
    start: RoundedPolygon,
    end: RoundedPolygon,
    atEnd: Boolean,
    animationSpec: FiniteAnimationSpec<Float> = MaterialTheme.motionScheme.defaultSpatialSpec<Float>(),
): Shape {
    val morph = rememberMorph(start, end)
    val progress by animateFloatAsState(
        targetValue = if (atEnd) 1f else 0f,
        animationSpec = animationSpec,
        label = "morphProgress",
    )
    return remember(morph, progress) {
        MorphPolygonShape(morph, progress)
    }
}

/**
 * Squishes the content to [pressedScale] while [interactionSource] reports a press,
 * and springs back on release, using the fast spatial motion spec.
 *
 * Composable (rather than `composed`) because it holds animated state.
 */
@Composable
fun Modifier.pressSquish(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.9f,
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec<Float>(),
        label = "pressSquishScale",
    )
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * A decorative container with a fixed size, its background clipped to [polygon].
 * [rotation] is applied to the background layer only — the [content] stays upright.
 */
@Composable
fun ShapeBadge(
    polygon: RoundedPolygon,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    rotation: Float = 0f,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val shape = polygon.toShape()
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { rotationZ = rotation }
                .background(containerColor, shape),
        )
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content()
        }
    }
}

/**
 * A decorative [polygon] that fills the given modifier's size and rotates endlessly,
 * one full turn every [periodMillis] (linear, since it is a pure decorative spin).
 */
@Composable
fun RotatingShape(
    polygon: RoundedPolygon,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    periodMillis: Int = 24_000,
) {
    val shape = polygon.toShape()
    val transition = rememberInfiniteTransition(label = "rotatingShape")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = periodMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rotatingShapeRotation",
    )
    Box(
        modifier = modifier
            .graphicsLayer { rotationZ = rotation }
            .background(color, shape),
    )
}

/**
 * The expressive morphing play/pause control used by the stopwatch and timers.
 *
 * Morphs from a cookie-9-sided shape (stopped) to a rounded square (running),
 * animates container/content colors, cross-morphs the Play/Pause icon, squishes on
 * press, fires toggle haptics, and spins one full turn per 12s while running.
 * When paused, the rotation springs back to the nearest multiple of 40°
 * (the 9-sided symmetry step).
 *
 * @param isRunning whether the tracked instrument is running
 * @param onClick called when the button is pressed
 * @param size the diameter of the button
 * @param enabled whether the button accepts clicks
 */
@Composable
fun MorphingPlayPauseButton(
    isRunning: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    enabled: Boolean = true,
) {
    val colorScheme = MaterialTheme.colorScheme
    // MaterialShapes.Square already carries some corner rounding, so the "stop"
    // state reads as a rounded square rather than a hard 90° corner.
    val endShape = MaterialShapes.Square
    val shape = animatedMorphShape(MaterialShapes.Cookie9Sided, endShape, atEnd = isRunning)

    val containerColor by animateColorAsState(
        targetValue = if (isRunning) colorScheme.tertiaryContainer else colorScheme.primary,
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "playPauseContainerColor",
    )
    val contentColor by animateColorAsState(
        targetValue = if (isRunning) colorScheme.onTertiaryContainer else colorScheme.onPrimary,
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "playPauseContentColor",
    )

    val iconSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val interactionSource = remember { MutableInteractionSource() }
    val hapticFeedback = LocalHapticFeedback.current

    // Gentle continuous spin while running (one turn per 12s). When paused, the
    // rotation springs back to the nearest multiple of 40° (the 9-sided symmetry step).
    // `rotation.value` is only read inside the `graphicsLayer` lambda, never in
    // composition, so the spin does not trigger button recompositions.
    val rotation = remember { Animatable(0f) }
    val settleSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (true) {
                rotation.animateTo(rotation.value + 360f, tween(durationMillis = 12_000, easing = LinearEasing))
                rotation.snapTo(rotation.value % 360f)
            }
        } else {
            rotation.animateTo((rotation.value / 40f).roundToInt() * 40f, settleSpec)
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .pressSquish(interactionSource)
            .semantics { role = Role.Button }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
            ) {
                hapticFeedback.performHapticFeedback(
                    if (isRunning) HapticFeedbackType.ToggleOff else HapticFeedbackType.ToggleOn,
                )
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { rotationZ = rotation.value }
                .background(containerColor, shape),
        )
        AnimatedContent(
            targetState = isRunning,
            contentKey = { it },
            transitionSpec = {
                (fadeIn(animationSpec = iconSpec) + scaleIn(initialScale = 0.6f, animationSpec = iconSpec)) togetherWith
                    (fadeOut(animationSpec = iconSpec) + scaleOut(targetScale = 0.6f, animationSpec = iconSpec))
            },
            label = "playPauseIcon",
        ) { running ->
            Icon(
                imageVector = if (running) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = stringResource(if (running) R.string.m3e_pause else R.string.m3e_start),
                modifier = Modifier.size(size * 0.42f),
                tint = contentColor,
            )
        }
    }
}

@Preview
@Composable
private fun ShapeBadgePreview() {
    ClockTheme {
        Box(
            modifier = Modifier
                .size(180.dp)
                .background(MaterialTheme.colorScheme.surface),
        ) {
            ShapeBadge(
                polygon = MaterialShapes.Sunny,
                modifier = Modifier.align(Alignment.TopStart),
            )
            ShapeBadge(
                polygon = MaterialShapes.Cookie12Sided,
                modifier = Modifier.align(Alignment.Center),
                rotation = 15f,
            )
            ShapeBadge(
                polygon = MaterialShapes.Flower,
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }
    }
}

@Preview
@Composable
private fun MorphingPlayPausePreview() {
    ClockTheme {
        Box(
            modifier = Modifier
                .size(240.dp)
                .background(MaterialTheme.colorScheme.surface),
        ) {
            MorphingPlayPauseButton(
                isRunning = false,
                onClick = {},
                modifier = Modifier.align(Alignment.CenterStart),
            )
            MorphingPlayPauseButton(
                isRunning = true,
                onClick = {},
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }
    }
}

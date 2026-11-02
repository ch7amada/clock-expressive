package org.fossify.clock.ui.clock

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.fossify.clock.ui.components.RotatingShape
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

/**
 * The expressive analogue face: a slowly rotating `Cookie12Sided` dial (one turn per
 * minute) with the hands, 12 markers and the center cap drawn in a single [Canvas]
 * on top, so they do not rotate with it. The second hand ticks to each new second
 * with a springy overshoot, using an ever-increasing angle so it never spins
 * backwards at :00.
 */
@Composable
fun AnalogClock(
    nowMillis: Long,
    modifier: Modifier = Modifier,
) {
    val zonedDateTime = Instant.ofEpochMilli(nowMillis).atZone(ZoneId.systemDefault())
    val hours = zonedDateTime.hour
    val minutes = zonedDateTime.minute
    val seconds = zonedDateTime.second

    val colorScheme = MaterialTheme.colorScheme
    val density = LocalDensity.current

    // Ever-increasing second-hand angle: each tick adds the delta modulo 360, so
    // the 59 -> 0 step animates a short 6-degree tick instead of a backward spin.
    val cumulativeAngle = remember { mutableFloatStateOf(seconds * 6f) }
    var lastSecond by remember { mutableIntStateOf(seconds) }
    val secondAngle = remember { Animatable(cumulativeAngle.floatValue) }
    val tickSpec = spring<Float>(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium)
    LaunchedEffect(seconds) {
        if (seconds != lastSecond) {
            val delta = ((seconds - lastSecond + 60) % 60) * 6f
            lastSecond = seconds
            cumulativeAngle.floatValue += delta
            secondAngle.animateTo(cumulativeAngle.floatValue, animationSpec = tickSpec)
        }
    }

    val hourAngle = (hours % 12 + minutes / 60f) * 30f
    val minuteAngle = (minutes + seconds / 60f) * 6f
    val timeText = DateTimeFormatter.ofPattern("HH:mm:ss").format(zonedDateTime)

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val clockSize = maxWidth.coerceAtMost(280.dp)
        Box(
            modifier = Modifier
                .size(clockSize)
                .semantics { contentDescription = timeText },
        ) {
            RotatingShape(
                polygon = MaterialShapes.Cookie12Sided,
                modifier = Modifier.matchParentSize(),
                color = colorScheme.surfaceContainerHigh,
                periodMillis = 60_000,
            )
            Canvas(modifier = Modifier.matchParentSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2f
                fun handEnd(angleDegrees: Float, lengthFraction: Float): Offset {
                    val radians = Math.toRadians((angleDegrees - 90).toDouble())
                    val length = radius * lengthFraction
                    return Offset(center.x + length * cos(radians).toFloat(), center.y + length * sin(radians).toFloat())
                }

                // Markers: 12 dots at 82% radius, the quarter-hour ones larger and in primary.
                for (index in 0 until 12) {
                    val position = handEnd(index * 30f, 0.82f)
                    val isQuarter = index % 3 == 0
                    val dotRadius = with(density) { (if (isQuarter) 7.dp else 4.dp).toPx() }
                    drawCircle(
                        color = if (isQuarter) colorScheme.primary else colorScheme.outlineVariant,
                        radius = dotRadius,
                        center = position,
                    )
                }

                // Hour, minute and second hands.
                drawLine(
                    color = colorScheme.primary,
                    start = center,
                    end = handEnd(hourAngle, 0.5f),
                    strokeWidth = with(density) { 14.dp.toPx() },
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = colorScheme.tertiary,
                    start = center,
                    end = handEnd(minuteAngle, 0.72f),
                    strokeWidth = with(density) { 10.dp.toPx() },
                    cap = StrokeCap.Round,
                )
                val secondEnd = handEnd(secondAngle.value, 0.82f)
                drawLine(
                    color = colorScheme.secondary,
                    start = center,
                    end = secondEnd,
                    strokeWidth = with(density) { 3.dp.toPx() },
                    cap = StrokeCap.Round,
                )
                drawCircle(
                    color = colorScheme.secondary,
                    radius = with(density) { 5.dp.toPx() },
                    center = secondEnd,
                )

                // Center cap.
                drawCircle(
                    color = colorScheme.primary,
                    radius = with(density) { 8.dp.toPx() },
                    center = center,
                )
                drawCircle(
                    color = colorScheme.onPrimary,
                    radius = with(density) { 3.dp.toPx() },
                    center = center,
                )
            }
        }
    }
}

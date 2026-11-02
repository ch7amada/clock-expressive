package org.fossify.clock.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlarmAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import org.fossify.clock.ui.theme.ClockTheme

/**
 * The expressive empty-state placeholder shown when a list has no entries.
 *
 * A 168.dp [polygon] rotates slowly in the background ([RotatingShape]), the [icon] sits
 * centered on top of it, followed by the [title], an optional [body] and an optional
 * [action] (usually a button). The whole column enters with a spring scale-in from 0.8f.
 *
 * @param icon the main icon rendered on top of the rotating shape
 * @param title the primary text, rendered with `headlineSmallEmphasized`
 * @param modifier modifier for the whole column
 * @param body optional secondary text, rendered with `bodyLarge` in `onSurfaceVariant`
 * @param polygon the decorative polygon behind the icon
 * @param action optional composable (e.g. a button) rendered below the texts
 */
@Composable
fun ExpressiveEmptyState(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    polygon: RoundedPolygon = MaterialShapes.Cookie12Sided,
    action: (@Composable () -> Unit)? = null,
) {
    val colorScheme = MaterialTheme.colorScheme
    // One-shot spring scale-in on entry: start at 0.8f, animate to 1f.
    val scale = remember { Animatable(0.8f) }
    val enterSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    LaunchedEffect(Unit) {
        scale.animateTo(1f, enterSpec)
    }
    Column(
        modifier = modifier.graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(168.dp),
            contentAlignment = Alignment.Center,
        ) {
            RotatingShape(
                polygon = polygon,
                color = colorScheme.secondaryContainer,
                modifier = Modifier.matchParentSize(),
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = colorScheme.onSecondaryContainer,
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmallEmphasized,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp),
        )
        if (body != null) {
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        if (action != null) {
            Spacer(modifier = Modifier.height(24.dp))
            action()
        }
    }
}

@Preview
@Composable
private fun ExpressiveEmptyStatePreview() {
    ClockTheme {
        ExpressiveEmptyState(
            icon = Icons.Rounded.AlarmAdd,
            title = "No alarms",
            body = "Tap the button to create your first alarm.",
        )
    }
}

package org.fossify.clock.ui.clock

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.fossify.clock.ui.components.AnimatedTimeText
import org.fossify.clock.ui.theme.ClockTextStyles
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * The visual centerpiece of the clock tab: hours and minutes as animated digits
 * in Google Sans Flex ([ClockTextStyles.heroClock], breathing weight 350–450 on
 * every second), a seconds pill in `primaryContainer`, and the AM/PM marker
 * (12h mode) stacked above the pill.
 */
@Composable
fun HeroClock(
    nowMillis: Long,
    use24h: Boolean,
    modifier: Modifier = Modifier,
) {
    val zonedDateTime = Instant.ofEpochMilli(nowMillis).atZone(ZoneId.systemDefault())
    val hours = zonedDateTime.hour
    val seconds = zonedDateTime.second

    val locale = Locale.getDefault()
    val timeFormatter = DateTimeFormatter.ofPattern(
        if (use24h) "HH:mm" else "h:mm",
        locale,
    )
    val timeText = timeFormatter.format(zonedDateTime)
    val secondsText = DateTimeFormatter.ofPattern("ss", locale).format(zonedDateTime)
    val amPmText: String? = if (use24h) {
        null
    } else {
        stringResource(if (hours >= 12) org.fossify.commons.R.string.p_m else org.fossify.commons.R.string.a_m)
    }

    // Breathing weight: 350–450, one full swing per second. Rendering snaps to a
    // remembered set of three styles instead of building a FontFamily per frame.
    val slowSpec = MaterialTheme.motionScheme.slowEffectsSpec<Int>()
    val weight by animateIntAsState(
        targetValue = if (seconds % 2 == 0) 450 else 350,
        animationSpec = slowSpec,
        label = "heroWeight",
    )
    val heroStyles = remember {
        listOf(
            ClockTextStyles.heroWith(350),
            ClockTextStyles.heroWith(400),
            ClockTextStyles.heroWith(450),
        )
    }
    val heroStyle = heroStyles[((weight - 350) / 50).coerceIn(0, heroStyles.size - 1)]

    val timeTextComposable: @Composable () -> Unit = {
        AnimatedTimeText(
            text = timeText,
            style = heroStyle,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
    val secondsPillComposable: @Composable () -> Unit = {
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            AnimatedTimeText(
                text = secondsText,
                style = MaterialTheme.typography.titleLargeEmphasized,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            )
        }
    }
    val amPmComposable: (@Composable () -> Unit)? = amPmText?.let { text ->
        {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLargeEmphasized,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }

    BoxWithConstraints(modifier = modifier) {
        val narrow = maxWidth < 340.dp
        if (narrow) {
            // seconds pill below the time on narrow widths
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center),
            ) {
                timeTextComposable()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    amPmComposable?.invoke()
                    secondsPillComposable()
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.align(Alignment.Center),
            ) {
                timeTextComposable()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(start = 12.dp, bottom = 4.dp),
                ) {
                    amPmComposable?.invoke()
                    secondsPillComposable()
                }
            }
        }
    }
}

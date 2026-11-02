package org.fossify.clock.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import org.fossify.clock.ui.theme.ClockTheme
import org.fossify.clock.ui.theme.ClockTextStyles

/**
 * Renders a time string (e.g. "07:42:09") character by character in a [Row].
 *
 * Every digit slot is an [AnimatedContent] with stable per-index identity: when the digit
 * changes, the old digit slides up and out while the new one slides up in from below, with
 * a fading cross-fade. Non-digit characters (`:`, `.`, spaces, AM/PM letters) are plain
 * [Text]s and never animate. Each slot is clipped so sliding digits never overlap their
 * neighbours.
 *
 * TalkBack reads the whole string once: the row uses [clearAndSetSemantics] with the full
 * text as [contentDescription], so the per-character children are not announced individually.
 *
 * @param text the full time string to display
 * @param style the text style applied to every character (tabular digits recommended)
 * @param modifier modifier for the whole row
 * @param color the text color (defaults to the current content color)
 */
@Composable
fun AnimatedTimeText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
) {
    // Motion specs must be read in composition, not inside the transition lambda.
    val spatialSpec = MaterialTheme.motionScheme.fastSpatialSpec<IntOffset>()
    val effectsSpec = MaterialTheme.motionScheme.fastEffectsSpec<Float>()
    Row(
        modifier = modifier.clearAndSetSemantics {
            contentDescription = text
        },
    ) {
        for (index in text.indices) {
            TimeCharacterSlot(
                index = index,
                char = text[index],
                style = style,
                color = color,
                spatialSpec = spatialSpec,
                effectsSpec = effectsSpec,
            )
        }
    }
}

/**
 * A single character slot of [AnimatedTimeText]. Digits animate via [AnimatedContent]
 * (stable identity per [index], so only changed slots animate); other characters are
 * static. The slot is clipped so sliding digits do not overlap neighbours.
 */
@Composable
private fun TimeCharacterSlot(
    index: Int,
    char: Char,
    style: TextStyle,
    color: Color,
    spatialSpec: androidx.compose.animation.core.FiniteAnimationSpec<IntOffset>,
    effectsSpec: androidx.compose.animation.core.FiniteAnimationSpec<Float>,
) {
    if (char.isDigit()) {
        Box(modifier = Modifier.clipToBounds()) {
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    (slideInVertically(initialOffsetY = { it }, animationSpec = spatialSpec) +
                        fadeIn(animationSpec = effectsSpec)) togetherWith
                        (slideOutVertically(targetOffsetY = { -it }, animationSpec = spatialSpec) +
                            fadeOut(animationSpec = effectsSpec))
                },
                label = "digit$index",
            ) { digit ->
                Text(text = digit.toString(), style = style, color = color)
            }
        }
    } else {
        Text(text = char.toString(), style = style, color = color)
    }
}

@Preview
@Composable
private fun AnimatedTimeTextPreview() {
    ClockTheme {
        Column {
            AnimatedTimeText(text = "07:42:09", style = ClockTextStyles.heroClockCompact)
        }
    }
}

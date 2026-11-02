package org.fossify.clock.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.fossify.clock.ui.theme.ClockTheme

/**
 * A section header for expressive grouped lists: `labelLarge` in `primary`,
 * with 16.dp start, 24.dp top and 8.dp bottom padding.
 *
 * @param text the header text
 * @param modifier modifier for the header
 */
@Composable
fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
    )
}

/**
 * Emits [count] lazy items meant to be rendered as one segmented group: each item receives
 * [ListItemDefaults.segmentedShapes] for its position in the group (so the first and last
 * items are rounded more strongly while the middle ones stay squared), and the items are
 * separated vertically by [ListItemDefaults.SegmentedGap].
 *
 * Because [ListItemDefaults.segmentedShapes] is @Composable, it is called inside the item
 * lambda. Callers provide the item composable (usually a [SegmentedListItem]) and receive
 * both the index and the precomputed shapes:
 *
 * ```
 * segmentedGroup(count = alarms.size) { index, shapes ->
 *     SegmentedListItem(onClick = { ... }, shapes = shapes, ...) { ... }
 * }
 * ```
 *
 * @param count the number of items in the group
 * @param key optional key factory per index (null = positional key)
 * @param itemContent composable receiving the item index and its [ListItemShapes]
 */
fun LazyListScope.segmentedGroup(
    count: Int,
    key: ((Int) -> Any)? = null,
    itemContent: @Composable (index: Int, shapes: ListItemShapes) -> Unit,
) {
    repeat(count) { index ->
        item(key = key?.invoke(index)) {
            val shapes = ListItemDefaults.segmentedShapes(index = index, count = count)
            Column(
                modifier = if (index > 0) {
                    Modifier.padding(top = ListItemDefaults.SegmentedGap)
                } else {
                    Modifier
                },
            ) {
                itemContent(index, shapes)
            }
        }
    }
}

@Preview
@Composable
private fun SegmentedPreview() {
    ClockTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    SectionHeader(text = "Alarms")
                }
                segmentedGroup(count = 3) { index, shapes ->
                    SegmentedListItem(
                        onClick = {},
                        shapes = shapes,
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Rounded.Alarm,
                                contentDescription = null,
                            )
                        },
                        supportingContent = {
                            Text(text = "Every day")
                        },
                    ) {
                        Text(text = "07:$index")
                    }
                }
            }
        }
    }
}

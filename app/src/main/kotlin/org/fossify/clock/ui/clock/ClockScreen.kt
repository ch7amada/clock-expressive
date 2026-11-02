package org.fossify.clock.ui.clock

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.TimeZone
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.collectAsState
import org.fossify.clock.R
import org.fossify.clock.extensions.getFormattedDate
import org.fossify.clock.ui.components.AnimatedTimeText
import org.fossify.clock.ui.components.SectionHeader
import org.fossify.clock.ui.components.ShapeBadge
import org.fossify.clock.ui.components.segmentedGroup
import org.fossify.clock.ui.navigation.ClockTopAppBar
import java.time.Instant
import java.time.ZoneId

/**
 * The clock tab: a hero digital clock with breathing weight, the current date,
 * a tappable "next alarm" [AssistChip] and the selected world-clock time zones
 * as a segmented list. Refreshes on every resume and on [org.fossify.clock.models.AlarmEvent.Refresh].
 */
@Composable
fun ClockScreen(
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenAlarms: () -> Unit,
    onAddTimeZone: () -> Unit,
    viewModel: ClockViewModel = viewModel(),
) {
    val context = LocalContext.current
    val now by viewModel.now.collectAsState()
    val nextAlarm by viewModel.nextAlarm.collectAsState()
    val timeZones by viewModel.timeZones.collectAsState()
    val use24h = viewModel.use24h

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // Recompute the date only when the day changes.
    val dayStamp = remember(now) {
        Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault()).toLocalDate()
    }
    val dateText = remember(dayStamp) {
        val calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
            // Noon, to stay clear of DST edge days
            set(dayStamp.year, dayStamp.monthValue - 1, dayStamp.dayOfMonth, 12, 0, 0)
        }
        context.getFormattedDate(calendar)
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh()
    }

    val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()

    // Hoisted outside the item so the hero's 0.92→1.0 scale-in runs only on
    // first composition, not when the item scrolls back into the LazyColumn.
    val heroScale = remember { Animatable(0.92f) }
    LaunchedEffect(Unit) {
        heroScale.animateTo(1f, animationSpec = spatialSpec)
    }

    val hapticFeedback = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            ClockTopAppBar(
                title = stringResource(R.string.clock),
                scrollBehavior = scrollBehavior,
                onOpenSettings = onOpenSettings,
                onOpenAbout = onOpenAbout,
            )
        },
        floatingActionButton = {
            MediumFloatingActionButton(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                    onAddTimeZone()
                },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Public,
                    contentDescription = stringResource(R.string.m3e_add_time_zone),
                )
            }
        },
        // The top bar consumes the top system bars, the shell the bottom ones.
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            item {
                HeroClock(
                    nowMillis = now,
                    use24h = use24h,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = heroScale.value
                            scaleY = heroScale.value
                        }
                        .padding(top = 24.dp),
                )
            }
            item {
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
            }
            if (nextAlarm.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        AssistChip(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onOpenAlarms()
                            },
                            label = { Text(nextAlarm) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Alarm,
                                    contentDescription = null,
                                )
                            },
                            shape = MaterialTheme.shapes.large,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                        )
                    }
                }
            }
            if (timeZones.isNotEmpty()) {
                item {
                    SectionHeader(stringResource(R.string.m3e_world_clock))
                }
                segmentedGroup(count = timeZones.size, key = { timeZones[it].id }) { index, shapes ->
                    val timeZone = timeZones[index]
                    SegmentedListItem(
                        onClick = {},
                        shapes = shapes,
                        leadingContent = {
                            ShapeBadge(
                                polygon = MaterialShapes.Cookie6Sided,
                                size = 48.dp,
                            ) {
                                Text(
                                    text = timeZone.offsetLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                )
                            }
                        },
                        supportingContent = {
                            Text(text = timeZone.dayLabel ?: timeZone.offsetLabel)
                        },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AnimatedTimeText(
                                    text = timeZone.time,
                                    style = MaterialTheme.typography.titleLargeEmphasized,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                timeZone.amPm?.let { amPm ->
                                    Text(
                                        text = " $amPm",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        },
                    ) {
                        Text(text = timeZone.title)
                    }
                }
            }
            item {
                // Keep the FAB from covering the last row.
                Spacer(modifier = Modifier.height(96.dp))
            }
        }
    }
}

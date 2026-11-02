package org.fossify.clock.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import org.fossify.clock.R
import org.fossify.clock.helpers.TAB_ALARM
import org.fossify.clock.helpers.TAB_CLOCK
import org.fossify.clock.helpers.TAB_STOPWATCH
import org.fossify.clock.helpers.TAB_TIMER

/**
 * The four top-level tabs of the clock app, in bottom-bar order.
 *
 * @param tabId the legacy intent/config tab id (see Constants)
 * @param label string resource id for the tab label
 * @param icon icon used while the tab is not selected
 * @param selectedIcon icon used while the tab is selected
 */
enum class ClockTab(
    val tabId: Int,
    @StringRes val label: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    CLOCK(TAB_CLOCK, R.string.clock, Icons.Outlined.Schedule, Icons.Filled.Schedule),
    ALARM(TAB_ALARM, org.fossify.commons.R.string.alarm, Icons.Outlined.Alarm, Icons.Filled.Alarm),
    STOPWATCH(TAB_STOPWATCH, R.string.stopwatch, Icons.Outlined.Timer, Icons.Filled.Timer),
    TIMER(TAB_TIMER, R.string.timer, Icons.Outlined.HourglassEmpty, Icons.Filled.HourglassBottom),
    ;

    companion object {
        /** Map a legacy tab id (e.g. from an `OPEN_TAB` intent extra) to its [ClockTab]. */
        fun fromTabId(id: Int): ClockTab? = entries.firstOrNull { it.tabId == id }
    }
}

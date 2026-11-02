package org.fossify.clock.ui.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("clock")
data object ClockRoute

@Serializable
@SerialName("alarm")
data object AlarmRoute

@Serializable
@SerialName("stopwatch")
data object StopwatchRoute

@Serializable
@SerialName("timer")
data object TimerRoute

/** The type-safe navigation route of this tab. */
fun ClockTab.route(): Any = when (this) {
    ClockTab.CLOCK -> ClockRoute
    ClockTab.ALARM -> AlarmRoute
    ClockTab.STOPWATCH -> StopwatchRoute
    ClockTab.TIMER -> TimerRoute
}

/**
 * Map a navigation destination route name (the serial name of the tab's route object)
 * back to its [ClockTab]. Returns null for unknown destinations.
 */
fun ClockTab.Companion.fromNavRoute(route: String?): ClockTab? = when (route) {
    "clock" -> ClockTab.CLOCK
    "alarm" -> ClockTab.ALARM
    "stopwatch" -> ClockTab.STOPWATCH
    "timer" -> ClockTab.TIMER
    else -> null
}

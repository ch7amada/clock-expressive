package org.fossify.clock.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.Flow
import org.fossify.clock.ui.clock.ClockScreen

/**
 * The expressive app shell: four top-level tabs (Clock, Alarm, Stopwatch, Timer) rendered
 * via navigation-compose, with a [ShortNavigationBar] on phones and a [WideNavigationRail]
 * on wide windows (≥ 600dp).
 *
 * @param startTab the tab shown on first launch
 * @param tabRequests tabs requested by new intents (e.g. shortcuts, notifications)
 * @param onTabChanged called whenever the visible tab changes (including the initial tab
 *   and non-tap changes such as Back); the activity uses it to persist
 *   `config.lastUsedViewPagerPage`
 * @param onOpenSettings opens the settings activity
 * @param onOpenAbout opens the about activity
 */
@Composable
fun ClockApp(
    startTab: ClockTab,
    tabRequests: Flow<ClockTab>,
    onTabChanged: (ClockTab) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val selectedTab = ClockTab.fromNavRoute(backStackEntry?.destination?.route) ?: startTab
    val hapticFeedback = LocalHapticFeedback.current

    // Fade-through transitions: the incoming tab fades + springs in from 0.96 while the
    // outgoing one fades out, both driven by the theme's motion scheme.
    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(effectsSpec) + scaleIn(initialScale = 0.96f, animationSpec = spatialSpec)
    }
    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(effectsSpec)
    }

    val navigateToTab: (ClockTab) -> Unit = { tab ->
        if (tab != selectedTab) {
            // tapping an already selected tab does nothing
            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
            navController.navigate(tab.route()) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    // The collector below runs once for the whole composition; read the lambda through
    // rememberUpdatedState so it always sees the *current* selectedTab, not the one from
    // the first composition.
    val currentNavigateToTab by rememberUpdatedState(navigateToTab)
    LaunchedEffect(Unit) {
        tabRequests.collect { tab ->
            currentNavigateToTab(tab)
        }
    }

    // Report the visible tab from the back stack, so persistence also covers non-tap
    // changes (e.g. Back popping to the start destination).
    LaunchedEffect(selectedTab) {
        onTabChanged(selectedTab)
    }

    val isWide = isWideWindow()
    if (isWide) {
        val railState = rememberWideNavigationRailState()
        val railExpanded = railState.currentValue == WideNavigationRailValue.Expanded
        Row(modifier = Modifier.fillMaxSize()) {
            WideNavigationRail(state = railState) {
                ClockTab.entries.forEach { tab ->
                    WideNavigationRailItem(
                        selected = tab == selectedTab,
                        onClick = { navigateToTab(tab) },
                        icon = { TabIcon(tab, selected = tab == selectedTab) },
                        label = { Text(stringResource(tab.label)) },
                        railExpanded = railExpanded,
                    )
                }
            }
            ClockTabNavHost(
                modifier = Modifier
                    .weight(1f)
                    // Pad only the bars the content itself must avoid: horizontal + bottom.
                    // Leave the top unpadded so the tab's app bar spans behind the status
                    // bar (its windowInsets cover that band), like in the phone branch.
                    .windowInsetsPadding(
                        TopAppBarDefaults.windowInsets
                            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                    ),
                navController = navController,
                startTab = startTab,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                onOpenSettings = onOpenSettings,
                onOpenAbout = onOpenAbout,
                onOpenAlarms = { navigateToTab(ClockTab.ALARM) },
            )
        }
    } else {
        Scaffold(
            contentWindowInsets = WindowInsets(0),
            bottomBar = {
                ShortNavigationBar {
                    ClockTab.entries.forEach { tab ->
                        ShortNavigationBarItem(
                            selected = tab == selectedTab,
                            onClick = { navigateToTab(tab) },
                            icon = { TabIcon(tab, selected = tab == selectedTab) },
                            label = { Text(stringResource(tab.label)) },
                        )
                    }
                }
            },
        ) { padding ->
            ClockTabNavHost(
                modifier = Modifier.padding(padding),
                navController = navController,
                startTab = startTab,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                onOpenSettings = onOpenSettings,
                onOpenAbout = onOpenAbout,
                onOpenAlarms = { navigateToTab(ClockTab.ALARM) },
            )
        }
    }
}

/** Whether the window is wide enough (≥ 600dp) for a navigation rail layout. */
@Composable
private fun isWideWindow(): Boolean {
    val windowSize = LocalWindowInfo.current.containerSize
    return with(LocalDensity.current) {
        windowSize.width.toDp() >= 600.dp
    }
}

@Composable
private fun ClockTabNavHost(
    modifier: Modifier,
    navController: NavHostController,
    startTab: ClockTab,
    enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition,
    exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenAlarms: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = startTab.route(),
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        modifier = modifier,
    ) {
        // F05+ replaces these placeholders with the real screens
        composable<ClockRoute> {
            ClockScreen(
                onOpenSettings = onOpenSettings,
                onOpenAbout = onOpenAbout,
                onOpenAlarms = onOpenAlarms,
                onAddTimeZone = {}, // F07
            )
        }
        composable<AlarmRoute> {
            TabPlaceholder(ClockTab.ALARM, onOpenSettings, onOpenAbout)
        }
        composable<StopwatchRoute> {
            TabPlaceholder(ClockTab.STOPWATCH, onOpenSettings, onOpenAbout)
        }
        composable<TimerRoute> {
            TabPlaceholder(ClockTab.TIMER, onOpenSettings, onOpenAbout)
        }
    }
}

/**
 * The tab item icon: the filled [ClockTab.selectedIcon] while selected, the outlined
 * [ClockTab.icon] otherwise, with a springy crossfade when the selection changes.
 */
@Composable
private fun TabIcon(tab: ClockTab, selected: Boolean) {
    val icon: ImageVector = if (selected) tab.selectedIcon else tab.icon
    val spec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    AnimatedContent(
        targetState = icon,
        transitionSpec = { fadeIn(spec) togetherWith fadeOut(spec) },
        label = "tabIcon",
    ) { vector ->
        Icon(
            imageVector = vector,
            contentDescription = null,
        )
    }
}

package org.fossify.clock.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import org.fossify.clock.R
import org.fossify.clock.ui.components.ExpressiveEmptyState

/**
 * Placeholder content of a tab until F05+ replaces it with the real screen.
 * A [Scaffold] with the shared [ClockTopAppBar] (collapsing on scroll) and an
 * [ExpressiveEmptyState] in the middle.
 */
@Composable
fun TabPlaceholder(
    tab: ClockTab,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            ClockTopAppBar(
                title = stringResource(tab.label),
                scrollBehavior = scrollBehavior,
                onOpenSettings = onOpenSettings,
                onOpenAbout = onOpenAbout,
            )
        },
        // The top bar consumes the top system bars, the shell (bottom bar / wide layout)
        // consumes the bottom ones, so the content needs no additional window insets.
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            ExpressiveEmptyState(
                icon = tab.selectedIcon,
                title = stringResource(tab.label),
                body = stringResource(R.string.m3e_coming_soon),
            )
        }
    }
}

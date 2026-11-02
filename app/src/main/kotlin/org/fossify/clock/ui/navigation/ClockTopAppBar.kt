package org.fossify.clock.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.RowScope

/**
 * The shared flexible top app bar of all tab screens: a [LargeFlexibleTopAppBar] with the
 * [title] in `displaySmallEmphasized`, optional [subtitle], screen-specific [actions] (e.g. a
 * sort button) followed by an overflow menu offering Settings and About.
 *
 * Each tab screen puts this into its own `Scaffold(topBar = ...)` and applies
 * `Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)` to its scrollable content.
 */
@Composable
fun ClockTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    var menuOpen by remember { mutableStateOf(false) }
    LargeFlexibleTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmallEmphasized,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        subtitle = subtitle?.let {
            {
                Text(
                    text = it,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        scrollBehavior = scrollBehavior,
        actions = {
            actions()
            IconButton(
                onClick = { menuOpen = true },
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = stringResource(org.fossify.commons.R.string.more_options),
                )
            }
            DropdownMenu(
                expanded = menuOpen,
                onDismissRequest = { menuOpen = false },
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(org.fossify.commons.R.string.settings)) },
                    onClick = {
                        menuOpen = false
                        onOpenSettings()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = null,
                        )
                    },
                    shape = MenuDefaults.shape,
                )
                DropdownMenuItem(
                    text = { Text(stringResource(org.fossify.commons.R.string.about)) },
                    onClick = {
                        menuOpen = false
                        onOpenAbout()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                        )
                    },
                    shape = MenuDefaults.shape,
                )
            }
        },
    )
}

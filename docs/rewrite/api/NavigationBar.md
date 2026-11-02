
### NavigationBar.kt
- `public fun NavigationBar( modifier: Modifier = Modifier, containerColor: Color = NavigationBarDefaults.containerColor, contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor), tonalElevation: Dp = NavigationBarDefaults.Elevation, windowInsets: WindowInsets = NavigationBarDefaults.windowInsets, content: @Composable RowScope.() -> Unit, )`
- `public fun RowScope.NavigationBarItem( selected: Boolean, onClick: () -> Unit, icon: @Composable () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, label: @Composable (() -> Unit)? = null, alwaysShowLabel: Boolean = true, colors: NavigationBarItemColors = NavigationBarItemDefaults.colors(), interactionSource: MutableInteractionSource? = null, )`
- `public fun colors(): NavigationBarItemColors`
- `public fun colors( selectedIconColor: Color = Color.Unspecified, selectedTextColor: Color = Color.Unspecified, indicatorColor: Color = Color.Unspecified, unselectedIconColor: Color = Color.Unspecified, unselectedTextColor: Color = Color.Unspecified, disabledIconColor: Color = Color.Unspecified, disabledTextColor: Color = Color.Unspecified, ): NavigationBarItemColors`

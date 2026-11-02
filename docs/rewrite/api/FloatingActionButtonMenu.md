
### FloatingActionButtonMenu.kt
- `public fun FloatingActionButtonMenu( expanded: Boolean, button: @Composable () -> Unit, modifier: Modifier = Modifier, horizontalAlignment: Alignment.Horizontal = Alignment.End, content: @Composable FloatingActionButtonMenuScope.() -> Unit, )`
- `public fun FloatingActionButtonMenuScope.FloatingActionButtonMenuItem( onClick: () -> Unit, text: @Composable () -> Unit, icon: @Composable () -> Unit, modifier: Modifier = Modifier, containerColor: Color = MaterialTheme.colorScheme.primaryContainer, contentColor: Color = contentColorFor(containerColor), )`
- `public fun ToggleFloatingActionButton( checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier, containerColor: (Float) -> Color = ToggleFloatingActionButtonDefaults.containerColor(), contentAlignment: Alignment = Alignment.TopEnd, containerSize: (Float) -> Dp = ToggleFloatingActionButtonDefaults.containerSize(), containerCornerRadius: (Float) -> Dp = ToggleFloatingActionButtonDefaults.containerCornerRadius(), content: @Composable ToggleFloatingActionButtonScope.() -> Unit, )`
- `public fun containerColor( initialColor: Color = MaterialTheme.colorScheme.primaryContainer, finalColor: Color = MaterialTheme.colorScheme.primary, ):`
- `public fun iconColor( initialColor: Color = MaterialTheme.colorScheme.onPrimaryContainer, finalColor: Color = MaterialTheme.colorScheme.onPrimary, ):`
- `public fun Modifier.animateIcon( checkedProgress: () -> Float, color: (Float) -> Color = iconColor(), size: (Float) -> Dp = iconSize(), ): Modifier`

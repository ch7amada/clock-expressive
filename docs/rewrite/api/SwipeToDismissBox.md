
### SwipeToDismissBox.kt
- `public fun rememberSwipeToDismissBoxState( initialValue: SwipeToDismissBoxValue = SwipeToDismissBoxValue.Settled, positionalThreshold: (totalDistance: Float) -> Float = SwipeToDismissBoxDefaults.positionalThreshold, ): SwipeToDismissBoxState`
- `public fun SwipeToDismissBox( state: SwipeToDismissBoxState, backgroundContent: @Composable RowScope.() -> Unit, modifier: Modifier = Modifier, enableDismissFromStartToEnd: Boolean = true, enableDismissFromEndToStart: Boolean = true, gesturesEnabled: Boolean = true, onDismiss: (SwipeToDismissBoxValue) -> Unit = {}, content: @Composable RowScope.() -> Unit, )`

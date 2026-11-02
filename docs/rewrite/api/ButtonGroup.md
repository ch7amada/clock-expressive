
### ButtonGroup.kt
- `public fun ButtonGroup( overflowIndicator: @Composable (ButtonGroupMenuState) -> Unit, modifier: Modifier = Modifier, @FloatRange(0.0) expandedRatio: Float = ButtonGroupDefaults.ExpandedRatio, horizontalArrangement: Arrangement.Horizontal = ButtonGroupDefaults.HorizontalArrangement, verticalAlignment: Alignment.Vertical = Alignment.Top, content: ButtonGroupScope.() -> Unit, )`
- `public fun connectedLeadingButtonShapes( shape: Shape = connectedLeadingButtonShape, pressedShape: Shape = connectedLeadingButtonPressShape, checkedShape: Shape = connectedButtonCheckedShape, ): ToggleButtonShapes`
- `public fun connectedMiddleButtonShapes( shape: Shape = ShapeDefaults.Small, pressedShape: Shape = connectedMiddleButtonPressShape, checkedShape: Shape = connectedButtonCheckedShape, ): ToggleButtonShapes`
- `public fun connectedTrailingButtonShapes( shape: Shape = connectedTrailingButtonShape, pressedShape: Shape = connectedTrailingButtonPressShape, checkedShape: Shape = connectedButtonCheckedShape, ): ToggleButtonShapes`
- `public fun OverflowIndicator( menuState: ButtonGroupMenuState, modifier: Modifier = Modifier, enabled: Boolean = true, shape: Shape = IconButtonDefaults.filledShape, colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(), interactionSource: MutableInteractionSource? = null, )`
- `public fun Modifier.weight( @FloatRange(from = 0.0, fromInclusive = false) weight: Float ): Modifier`
- `public fun Modifier.animateWidth(interactionSource: InteractionSource): Modifier`
- `public fun Modifier.animateWidth( interactionSource: InteractionSource, compressionLimit: Dp, ): Modifier`
- `public fun Modifier.align(alignment: Alignment.Vertical): Modifier`
- `fun ButtonGroupContent()`
- `fun MenuContent(state: ButtonGroupMenuState)`

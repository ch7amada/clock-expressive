
### SheetDefaults.kt
- `public fun requireOffset(): Float`
- `fun expand()`
- `fun partialExpand()`
- `fun show()`
- `fun hide()`
- `public fun Saver( enabledValues: Set<SheetValue>, positionalThreshold: () -> Float, velocityThreshold: () -> Float, confirmValueChange: (SheetValue) -> Boolean, ): Saver<SheetState, SheetValue>`
- `public fun Saver( skipPartiallyExpanded: Boolean, positionalThreshold: () -> Float, velocityThreshold: () -> Float, confirmValueChange: (SheetValue) -> Boolean, skipHiddenState: Boolean, ): Saver<SheetState, SheetValue>`
- `public fun DragHandle( modifier: Modifier = Modifier, width: Dp = SheetBottomTokens.DockedDragHandleWidth, height: Dp = SheetBottomTokens.DockedDragHandleHeight, shape: Shape = MaterialTheme.shapes.extraLarge, color: Color = SheetBottomTokens.DockedDragHandleColor.value, )`
- `public fun rememberBottomSheetState( initialValue: SheetValue, enabledValues: Set<SheetValue> = setOf(Hidden, PartiallyExpanded, Expanded), confirmValueChange: (SheetValue) -> Boolean = { true }, ): SheetState`
- `public val HiddenShape: Shape
- `public val ExpandedShape: Shape
- `public val Elevation: Dp`
- `public val SheetPeekHeight: Dp`
- `public val SheetMaxWidth: Dp`

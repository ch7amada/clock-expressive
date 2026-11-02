
### AlertDialog.kt
- `public fun BasicAlertDialog( onDismissRequest: () -> Unit, modifier: Modifier = Modifier, properties: DialogProperties = DialogProperties(), content: @Composable () -> Unit, )`
- `public fun AlertDialog( onDismissRequest: () -> Unit, modifier: Modifier = Modifier, properties: DialogProperties = DialogProperties(), content: @Composable () -> Unit, ): Unit`

### AndroidAlertDialog.android.kt — **the standard dialog; use this one**
```kotlin
@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,            // extraLarge
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties(),
)
```
The `AlertDialog(onDismissRequest) { content }` / `BasicAlertDialog` overloads draw **no container** (no surface, no shape,
no padding) — only use them if you wrap the content in your own `Surface(shape = AlertDialogDefaults.shape, color = AlertDialogDefaults.containerColor, tonalElevation = AlertDialogDefaults.TonalElevation)`.

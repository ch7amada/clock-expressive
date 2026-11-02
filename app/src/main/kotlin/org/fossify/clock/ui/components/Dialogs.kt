package org.fossify.clock.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.fossify.clock.ui.theme.ClockTheme

/**
 * A confirmation dialog with an optional [icon], a body [text] and two buttons:
 * the confirm [TextButton] labelled [confirmLabel] and a dismiss button labelled
 * [dismissLabel] (defaults to the Fossify commons "Cancel" string).
 *
 * @param title dialog title
 * @param text dialog body text
 * @param confirmLabel label of the confirm button
 * @param onConfirm called when the confirm button is pressed
 * @param onDismiss called when the dialog is dismissed (back, outside tap, dismiss button)
 * @param icon optional icon shown above the title
 * @param dismissLabel label of the dismiss button
 */
@Composable
fun ConfirmDialog(
    title: String,
    text: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    icon: ImageVector? = null,
    dismissLabel: String = stringResource(org.fossify.commons.R.string.cancel),
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissLabel)
            }
        },
        icon = icon?.let { { Icon(imageVector = it, contentDescription = null) } },
        title = { Text(text = title) },
        text = { Text(text = text) },
    )
}

/**
 * A dialog listing [options] as radio rows. Each row is selectable with
 * [Role.RadioButton]; selecting a row calls [onSelect] with its value and then [onDismiss].
 * The dialog container uses the default dialog shape (`extraLarge`).
 *
 * @param T type of the option values
 * @param title dialog title
 * @param options the selectable options as (value, label) pairs
 * @param selected the currently selected value
 * @param onSelect called with the value of the row the user picked
 * @param onDismiss called when the dialog is dismissed (back or outside tap)
 */
@Composable
fun <T> RadioListDialog(
    title: String,
    options: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(org.fossify.commons.R.string.cancel))
            }
        },
        title = { Text(text = title) },
        text = {
            Column(modifier = Modifier.selectableGroup()) {
                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .selectable(
                                selected = value == selected,
                                role = Role.RadioButton,
                                onClick = {
                                    onSelect(value)
                                    onDismiss()
                                },
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = value == selected,
                            onClick = null,
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        },
    )
}

@Preview
@Composable
private fun ConfirmDialogPreview() {
    ClockTheme {
        ConfirmDialog(
            title = "Delete alarm",
            text = "This alarm will be removed permanently.",
            confirmLabel = "Delete",
            icon = Icons.Rounded.Delete,
            onConfirm = {},
            onDismiss = {},
        )
    }
}

@Preview
@Composable
private fun RadioListDialogPreview() {
    ClockTheme {
        RadioListDialog(
            title = "Repeat days",
            options = listOf(1 to "Every day", 2 to "Weekdays", 3 to "Weekends"),
            selected = 1,
            onSelect = {},
            onDismiss = {},
        )
    }
}

package it.polimi.dima.track.common.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.polimi.dima.track.R

enum class IconButtonStyle {
  Normal, Filled, FilledTonal
}

@Composable
fun DropdownContextMenu(
  options: List<String>,
  modifier: Modifier,
  onActionClick: (String) -> Unit,
  style: IconButtonStyle = IconButtonStyle.Normal
) {
  var isExpanded by remember { mutableStateOf(false) }

  Box(modifier = modifier) {
    val icon: @Composable () -> Unit = {
      Icon(
        imageVector = Icons.Rounded.MoreVert,
        contentDescription = stringResource(R.string.more_options)
      )
    }
    val onButtonClick = { isExpanded = true }
    when (style) {
      IconButtonStyle.Normal -> IconButton(onClick = onButtonClick) { icon() }
      IconButtonStyle.Filled -> FilledIconButton(onClick = onButtonClick) { icon() }
      IconButtonStyle.FilledTonal -> FilledTonalIconButton(onClick = onButtonClick) { icon() }
    }

    /*
     * Utilizzo DropdownMenu invece che ExposedDropdownMenu perché quest'ultimo non permette di
     * ancorare il menu ad un elemento diverso da un TextField.
     */
    DropdownMenu(
      modifier = Modifier.width(180.dp),
      expanded = isExpanded,
      onDismissRequest = { isExpanded = false }
    ) {
      options.forEach { selectionOption ->
        DropdownMenuItem(
          /*
           * Possiamo anche usare una leading icon, ma in questo caso non è necessario.
           * https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#DropdownMenu(kotlin.Boolean,kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.unit.DpOffset,androidx.compose.ui.window.PopupProperties,kotlin.Function1)
           */
          text = { Text(text = selectionOption, style = MaterialTheme.typography.bodyLarge) },
          onClick = {
            isExpanded = false
            onActionClick(selectionOption)
          }
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
  @StringRes label: Int,
  options: List<String>,
  selection: String,
  modifier: Modifier,
  onNewValue: (String) -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
    expanded = isExpanded,
    modifier = modifier,
    onExpandedChange = { isExpanded = !isExpanded }
  ) {
    TextField(
      modifier = Modifier
        .fillMaxWidth()
        .menuAnchor(),
      readOnly = true,
      value = selection,
      onValueChange = {},
      label = { Text(stringResource(label)) },
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isExpanded) },
      colors = dropdownColors()
    )

    ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
      options.forEach { selectionOption ->
        DropdownMenuItem(
          text = { Text(text = selectionOption) },
          onClick = {
            onNewValue(selectionOption)
            isExpanded = false
          }
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun dropdownColors(): TextFieldColors {
  return ExposedDropdownMenuDefaults.textFieldColors(
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.primary
  )
}

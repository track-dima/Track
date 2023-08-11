package it.polimi.dima.track.common.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.polimi.dima.track.common.ext.dropdownSelector

@Composable
fun DangerousCardEditor(
  @StringRes title: Int,
  icon: ImageVector,
  text: String,
  modifier: Modifier,
  onEditClick: () -> Unit
) {
  CardEditor(title, icon, text, onEditClick, MaterialTheme.colorScheme.primary, modifier)
}

@Composable
fun RegularCardEditor(
  @StringRes title: Int,
  icon: ImageVector,
  text: String,
  modifier: Modifier,
  onEditClick: () -> Unit
) {
  CardEditor(title, icon, text, onEditClick, MaterialTheme.colorScheme.onSurface, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardEditor(
  @StringRes title: Int,
  icon: ImageVector,
  text: String,
  onEditClick: () -> Unit,
  highlightColor: Color,
  modifier: Modifier
) {
  Card(
    modifier = modifier,
    onClick = onEditClick
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(stringResource(title), color = highlightColor)
      }

      if (text.isNotBlank()) {
        Text(text = text, modifier = Modifier.padding(16.dp, 0.dp))
      }

      Icon(
        imageVector = icon,
        contentDescription = stringResource(title),
        tint = highlightColor
      )
    }
  }
}

@Composable
fun CardSelector(
  @StringRes label: Int,
  options: List<String>,
  selection: String,
  modifier: Modifier,
  onNewValue: (String) -> Unit
) {
  Card(modifier = modifier) {
    DropdownSelector(label, options, selection, Modifier.dropdownSelector(), onNewValue)
  }
}

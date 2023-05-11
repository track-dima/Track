package it.polimi.dima.track.common.composable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicToolbar(@StringRes title: Int) {
  TopAppBar(title = { Text(stringResource(title)) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionToolbar(
  @StringRes title: Int,
  startActionIcon: ImageVector? = null,
  endActionIcon: ImageVector,
  modifier: Modifier,
  startAction: (() -> Unit)? = null,
  endAction: () -> Unit
) {
  val hasStartAction = startActionIcon != null && startAction != null

  TopAppBar(
    title = {
      if (hasStartAction) {
          Text(
            stringResource(title),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )
      } else {
          Text(
            stringResource(title),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
      }
            },
    navigationIcon = {
      if (hasStartAction) {
        IconButton(onClick = startAction!!) {
          Icon(imageVector = startActionIcon!!, contentDescription = "Action")
        }
      }
    },
    actions = {
      Box(modifier) {
        IconButton(onClick = endAction) {
          Icon(imageVector = endActionIcon, contentDescription = "Action")
        }
      }
    }
  )
}

package it.polimi.dima.track.common.composable

import android.graphics.drawable.Icon
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import it.polimi.dima.track.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicToolbar(@StringRes title: Int) {
  TopAppBar(title = { Text(stringResource(title)) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoTitleToolbar(
  navigationIcon : @Composable () -> Unit,
  actions: @Composable RowScope.() -> Unit
) {
  TopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = Color.Transparent,
      scrolledContainerColor = Color.Transparent
    ),
    title = { },
    navigationIcon = navigationIcon,
    actions = actions
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionToolbar(
  @StringRes title: Int,
  modifier: Modifier,
  startActionIcon: ImageVector? = null,
  @StringRes startActionDescription: Int? = null,
  startAction: (() -> Unit)? = null,
  endActionIcon: ImageVector,
  @StringRes endActionDescription: Int,
  endAction: () -> Unit
) {
  val hasStartAction = startActionIcon != null && startAction != null && startActionDescription != null

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
          Icon(
            imageVector = startActionIcon!!,
            contentDescription = stringResource(startActionDescription!!)
          )
        }
      }
    },
    actions = {
      Box(modifier) {
        IconButton(onClick = endAction) {
          Icon(
            imageVector = endActionIcon,
            contentDescription = stringResource(endActionDescription)
          )
        }
      }
    }
  )
}

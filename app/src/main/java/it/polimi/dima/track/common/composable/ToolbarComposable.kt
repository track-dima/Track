package it.polimi.dima.track.common.composable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicToolbar(@StringRes title: Int) {
  TopAppBar(title = { Text(stringResource(title)) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionToolbar(
  @StringRes title: Int,
  @DrawableRes endActionIcon: Int,
  modifier: Modifier,
  endAction: () -> Unit
) {
  TopAppBar(
    title = { Text(stringResource(title)) },
    actions = {
      Box(modifier) {
        IconButton(onClick = endAction) {
          Icon(painter = painterResource(endActionIcon), contentDescription = "Action")
        }
      }
    }
  )
}

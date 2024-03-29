package it.polimi.dima.track.common.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import it.polimi.dima.track.R
import it.polimi.dima.track.common.ext.bigSpacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenDialog(
  onDismissRequest: () -> Unit,
  onConfirm: () -> Unit,
  title: String,
  content: @Composable () -> Unit
) {
  AlertDialog(
    modifier = Modifier.fillMaxSize(),
    properties = DialogProperties(usePlatformDefaultWidth = false),
    onDismissRequest = onDismissRequest,
  ) {
    Surface(
      modifier = Modifier
          .wrapContentWidth()
          .wrapContentHeight(),
      shape = MaterialTheme.shapes.large,
      tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
      Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          IconButton(onClick = onDismissRequest) {
            Icon(Icons.Rounded.Close, stringResource(R.string.close))
          }
          Text(
            modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp),
            text = title,
            style = MaterialTheme.typography.titleLarge,
          )
          Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(0.dp, 0.dp, 8.dp, 0.dp),
            horizontalArrangement = Arrangement.End
          ) {
            DialogConfirmButton (R.string.save){
              onConfirm()
            }
          }
        }
        Spacer(modifier = Modifier.bigSpacer())
        content()
      }
    }
  }
}

@Composable
fun DeleteDialog(
  title: String = stringResource(R.string.delete_repetition),
  text: String = stringResource(R.string.delete_repetition_confirmation),
  onDeleteClick: () -> Unit,
  onDismissRequest: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = { Text(text = title) },
    text = { Text(text = text) },
    confirmButton = {
      DialogConfirmButton(text = R.string.delete) {
        onDeleteClick()
      }
    },
    dismissButton = {
      DialogCancelButton(text = R.string.cancel) {
        onDismissRequest()
      }
    }
  )
}
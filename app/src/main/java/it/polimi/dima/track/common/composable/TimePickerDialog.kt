package it.polimi.dima.track.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
  confirmButton: @Composable () -> Unit,
  dismissButton: @Composable (() -> Unit)? = null,
  title: String = "Select Time",
  content: @Composable () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    modifier = modifier.wrapContentHeight(),
    properties = properties
  ) {
    Surface(
      shape = MaterialTheme.shapes.extraLarge,
      tonalElevation = 6.dp,
      modifier = Modifier
        .width(IntrinsicSize.Min)
        .height(IntrinsicSize.Min)
        .background(
          shape = MaterialTheme.shapes.extraLarge,
          color = MaterialTheme.colorScheme.surface
        ),
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
          text = title,
          style = MaterialTheme.typography.labelMedium
        )
        content()
        Row(
          modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()
        ) {
          Spacer(modifier = Modifier.weight(1f))
          dismissButton?.invoke()
          confirmButton()
        }
      }
    }
  }
}
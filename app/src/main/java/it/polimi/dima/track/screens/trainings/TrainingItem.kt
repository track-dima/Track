package it.polimi.dima.track.screens.trainings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.DropdownContextMenu
import it.polimi.dima.track.common.ext.contextMenu
import it.polimi.dima.track.common.ext.hasDueDate
import it.polimi.dima.track.common.ext.hasDueTime
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.ui.theme.DarkOrange
import java.lang.StringBuilder

@Composable
fun TrainingItem(
  training: Training,
  options: List<String>,
  onCheckChange: () -> Unit,
  onActionClick: (String) -> Unit
) {
  Card(
    modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Checkbox(
        checked = training.completed,
        onCheckedChange = { onCheckChange() },
        modifier = Modifier.padding(8.dp, 0.dp)
      )

      Column(modifier = Modifier.weight(1f)) {
        Text(text = training.title, style = MaterialTheme.typography.titleMedium)
        Text(text = getDueDateAndTime(training), fontSize = 12.sp)
      }

      if (training.flag) {
        Icon(
          painter = painterResource(R.drawable.ic_flag),
          tint = DarkOrange,
          contentDescription = "Flag"
        )
      }

      DropdownContextMenu(options, Modifier.contextMenu(), onActionClick)
    }
  }
}

private fun getDueDateAndTime(training: Training): String {
  val stringBuilder = StringBuilder("")

  if (training.hasDueDate()) {
    stringBuilder.append(training.dueDate)
    stringBuilder.append(" ")
  }

  if (training.hasDueTime()) {
    stringBuilder.append("at ")
    stringBuilder.append(training.dueTime)
  }

  return stringBuilder.toString()
}

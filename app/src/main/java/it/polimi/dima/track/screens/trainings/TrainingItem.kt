package it.polimi.dima.track.screens.trainings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import it.polimi.dima.track.common.ext.smallSpacer
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.screens.edit_training.EditTrainingViewModel
import it.polimi.dima.track.ui.theme.DarkOrange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

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
      /*verticalAlignment = Alignment.CenterVertically,*/
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp, 16.dp, 16.dp, 16.dp)
        .height(160.dp),
    ) {
      /*Checkbox(
        checked = training.completed,
        onCheckedChange = { onCheckChange() },
        modifier = Modifier.padding(8.dp, 0.dp)
      )*/

      Column(modifier = Modifier.weight(1f)) {
        Text(text = getDueDateAndTime(training), fontSize = 12.sp)
        Spacer(modifier = Modifier.smallSpacer())
        Text(text = training.title, style = MaterialTheme.typography.titleLarge)
        Text(text = training.description, style = MaterialTheme.typography.titleMedium)

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
          contentAlignment = Alignment.BottomStart
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            Text(text = "4 repetitions", style = MaterialTheme.typography.titleSmall)
            Text(text = "~1:30h", style = MaterialTheme.typography.titleSmall)
          }
        }
      }

      if (training.flag) {
        Icon(
          painter = painterResource(R.drawable.ic_flag),
          tint = DarkOrange,
          contentDescription = "Flag"
        )
      }
      Column(modifier = Modifier.width(IntrinsicSize.Min), horizontalAlignment = Alignment.End) {
        DropdownContextMenu(options, Modifier.contextMenu(), onActionClick)
        if (isScheduled(training)) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .fillMaxHeight(),
            contentAlignment = Alignment.BottomCenter
          ) {
            Icon(
              painter = painterResource(id = R.drawable.ic_calendar),
              tint = DarkOrange,
              contentDescription = "Scheduled training",
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }
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

private fun isScheduled(training: Training): Boolean {
  if (!training.hasDueDate()) {
    return false
  }

  val formatter = SimpleDateFormat(EditTrainingViewModel.DATE_FORMAT, Locale.ENGLISH)
  val dueDate = formatter.parse(training.dueDate)
  val currentDate = Calendar.getInstance(TimeZone.getTimeZone(EditTrainingViewModel.UTC)).time
  return currentDate.before(dueDate)
}

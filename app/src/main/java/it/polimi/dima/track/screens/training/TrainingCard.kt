package it.polimi.dima.track.screens.training

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.DropdownContextMenu
import it.polimi.dima.track.common.ext.calculateRepetitions
import it.polimi.dima.track.common.ext.calculateTotalTime
import it.polimi.dima.track.common.ext.contextMenu
import it.polimi.dima.track.common.ext.getDueDateAndTime
import it.polimi.dima.track.common.ext.isScheduled
import it.polimi.dima.track.common.ext.secondsToHhMm
import it.polimi.dima.track.common.ext.smallSpacer
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.Type
import it.polimi.dima.track.ui.theme.DarkOrange
import it.polimi.dima.track.ui.theme.DarkRed
import it.polimi.dima.track.ui.theme.DarkYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingCard(
  modifier: Modifier = Modifier,
  training: Training,
  actions: List<String> = listOf(),
  selected: Boolean = false,
  showActions: Boolean = true,
  onActionClick: (String) -> Unit = {},
  onClick: () -> Unit
) {
  Card(
    modifier = modifier,
    onClick = onClick,
    colors = if (selected) CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer
    ) else CardDefaults.cardColors(),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp, 16.dp, 16.dp, 16.dp)
        .height(160.dp),
    ) {
      Column(modifier = Modifier.weight(1f)) {
        TrainingCardContent(training)
      }

      Column(modifier = Modifier.width(IntrinsicSize.Min), horizontalAlignment = Alignment.End) {
        if (showActions) DropdownContextMenu(actions, Modifier.contextMenu(), onActionClick)
        if (training.isScheduled()) {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
          ) {
            Icon(
              imageVector = Icons.Rounded.Schedule,
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

@Composable
private fun TrainingCardContent(training: Training) {
  Text(text = training.getDueDateAndTime(), fontSize = 12.sp)
  Spacer(modifier = Modifier.smallSpacer())
  Text(
    text = training.title,
    style = MaterialTheme.typography.titleLarge,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis
  )
  Text(
    text = training.description,
    style = MaterialTheme.typography.titleMedium,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis
  )

  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.BottomStart
  ) {
    TrainingCardBottomInformation(training)
  }
}

@Composable
private fun TrainingCardBottomInformation(training: Training) {
  Row(
    modifier = Modifier.height(24.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    val repetitions = training.calculateRepetitions()

    if (training.favorite) {
      Icon(
        imageVector = Icons.Rounded.Favorite,
        tint = DarkRed,
        contentDescription = stringResource(id = R.string.favorite),
      )
    }
    if (training.personalBest) {
      Icon(
        imageVector = Icons.Rounded.EmojiEvents,
        tint = DarkYellow,
        contentDescription = stringResource(id = R.string.personal_best),
      )
    }
    Text(
      text = repetitions.toString() + " repetition" + if (repetitions > 1) "s" else "",
      style = MaterialTheme.typography.titleSmall
    )
    Text(
      text = "~${training.calculateTotalTime().secondsToHhMm()}h",
      style = MaterialTheme.typography.titleSmall
    )
    if (training.type.isNotEmpty() && training.type != Type.None.name) {
      Text(text = training.type, style = MaterialTheme.typography.titleSmall)
    }
  }
}

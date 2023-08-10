package it.polimi.dima.track.common.composable

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowLeft
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.DragIndicator
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.InsertChartOutlined
import androidx.compose.material.icons.rounded.MoreTime
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import it.polimi.dima.track.EDIT_REPETITIONS_SCREEN
import it.polimi.dima.track.FILL_REPETITIONS_SCREEN
import it.polimi.dima.track.R
import it.polimi.dima.track.TRAINING_ID
import it.polimi.dima.track.common.ext.fieldModifier
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.TrainingStep
import it.polimi.dima.track.screens.edit_repetitions.removeLeadingZeros
import it.polimi.dima.track.screens.edit_repetitions.secondsToHhMmSs
import org.burnoutcrew.reorderable.ItemPosition
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarmUpCard(
  trainingStep: TrainingStep,
  onDeleteClick: (List<String>, TrainingStep) -> Unit = { _, _ -> },
  onEditClick: (List<String>, TrainingStep) -> Unit = { _, _ -> },
  readOnly: Boolean = false
) {
  Card(
    modifier = Modifier
      .fieldModifier()
      .fillMaxWidth()
      .height(70.dp),
    onClick = { onEditClick(listOf(trainingStep.id), trainingStep) },
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.secondaryContainer
    )
  ) {
    StepCardHeader(
      modifier = Modifier.fillMaxHeight(),
      stepType = TrainingStep.Type.WARM_UP,
      onDeleteClick = { onDeleteClick(listOf(trainingStep.id), trainingStep) },
      readOnly = readOnly
    ) {
      Column(
        modifier = Modifier.padding(8.dp, 0.dp)
      ) {
        Row {
          Text(text = stringResource(id = R.string.warm_up))
        }
        Row {
          Text(
            text = if (trainingStep.durationType == TrainingStep.DurationType.TIME)
              secondsToHhMmSs(trainingStep.duration)
            else "${trainingStep.distance} ${trainingStep.distanceUnit}",
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoolDownCard(
  trainingStep: TrainingStep,
  onDeleteClick: (List<String>, TrainingStep) -> Unit = { _, _ -> },
  onEditClick: (List<String>, TrainingStep) -> Unit = { _, _ -> },
  readOnly: Boolean = false
) {
  Card(
    modifier = Modifier
      .fieldModifier()
      .fillMaxWidth()
      .height(70.dp),
    onClick = { onEditClick(listOf(trainingStep.id), trainingStep) },
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.secondaryContainer,
    )
  ) {
    StepCardHeader(
      modifier = Modifier.fillMaxHeight(),
      stepType = TrainingStep.Type.COOL_DOWN,
      onDeleteClick = { onDeleteClick(listOf(trainingStep.id), trainingStep) },
      readOnly = readOnly
    ) {
      Column(
        modifier = Modifier.padding(8.dp, 0.dp)
      ) {
        Row {
          Text(text = stringResource(id = R.string.cool_down))
        }
        Row {
          Text(
            text = if (trainingStep.durationType == TrainingStep.DurationType.TIME)
              secondsToHhMmSs(trainingStep.duration)
            else "${trainingStep.distance} ${trainingStep.distanceUnit}",
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepetitionsCard(
  trainingStep: TrainingStep,
  showRecover: Boolean,
  onDeleteClick: (List<String>, TrainingStep) -> Unit = { _, _ -> },
  onEditClick: (List<String>, TrainingStep) -> Unit = { _, _ -> },
  onTimeFillClick: (List<String>, TrainingStep) -> Unit = { _, _ -> },
  readOnly: Boolean = false,
  fillTime: Boolean = false,
  resultIndex: Int = 0
) {
  Card(
    modifier = Modifier
      .fieldModifier()
      .fillMaxWidth()
      .height(70.dp),
    onClick = {
      if (fillTime) onTimeFillClick(listOf(), trainingStep) else onEditClick(
        listOf(
          trainingStep.id
        ), trainingStep
      )
    },
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.secondaryContainer,
    )
  ) {
    StepCardHeader(
      modifier = Modifier.fillMaxHeight(),
      stepType = TrainingStep.Type.REPETITION,
      onDeleteClick = { onDeleteClick(listOf(trainingStep.id), trainingStep) },
      readOnly = readOnly
    ) {
      Column(
        modifier = Modifier.padding(8.dp, 0.dp)
      ) {
        Column {
          Text(
            text = if (trainingStep.durationType == TrainingStep.DurationType.TIME)
              secondsToHhMmSs(trainingStep.duration)
            else "${trainingStep.distance} ${trainingStep.distanceUnit}",
            fontWeight = FontWeight.Bold
          )
          if (showRecover && trainingStep.recover) {
            Row {
              Text(text = "Recover ")
              Text(
                text = if (trainingStep.recoverType == TrainingStep.DurationType.TIME)
                  secondsToHhMmSs(trainingStep.recoverDuration)
                else "${trainingStep.recoverDistance} ${trainingStep.recoverDistanceUnit}",
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
      if (readOnly || fillTime) {
        Column(
          modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(end = 16.dp),
          horizontalAlignment = Alignment.End,
          verticalArrangement = Arrangement.Center
        ) {
          if (trainingStep.results.size > resultIndex && trainingStep.results[resultIndex].isNotEmpty()) {
            Text(
              // TODO tasto reset e non mostrare se 0
              text = removeLeadingZeros(trainingStep.results[resultIndex]),
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
fun RepetitionBlockCard(
  repetitionBlock: TrainingStep,
  onDeleteClick: (List<String>, TrainingStep) -> Unit = { _, _ -> },
  onEditClick: (List<String>, TrainingStep) -> Unit = { _, _ -> },
  onAddClick: (List<String>) -> Unit = { _ -> },
  onAddBlockClick: (List<String>, Int) -> Unit = { _, _ -> },
  onRepetitionsClick: (List<String>, Int) -> Unit = { _, _ -> },
  onRecoverClick: (List<String>, String, Int, Int, String, Boolean) -> Unit = { _, _, _, _, _, _ -> },
  onMove: (List<String>, ItemPosition, ItemPosition) -> Unit = { _, _, _ -> },
  onTimeFillClick: (List<String>, Int, TrainingStep) -> Unit = { _, _, _ -> },
  readOnly: Boolean = false,
  lastStep: Boolean = false,
  fillTime: Boolean = false,
  level: Int = 0
) {
  val tree = repetitionBlock.calculateTree()

  OutlinedCard(
    modifier = Modifier
      .fieldModifier()
      .fillMaxWidth()
      .height(78.dp * (tree.first) + (if (readOnly) 70.dp else 156.dp) * (tree.second))
  ) {
    val currentIndex = rememberSaveable { mutableStateOf(0) }

    StepCardHeader(
      stepType = TrainingStep.Type.REPETITION_BLOCK,
      onDeleteClick = { onDeleteClick(listOf(), repetitionBlock) },
      readOnly = readOnly
    ) {
      Row(
        modifier = if (readOnly) Modifier.fillMaxWidth() else Modifier,
        horizontalArrangement = if (readOnly) Arrangement.Center else Arrangement.Start
      ) {
        if (fillTime || readOnly) {
          TextButton(onClick = { currentIndex.value = maxOf(currentIndex.value - 1 , 0) }) {
            Icon(
              Icons.Rounded.ArrowLeft,
              contentDescription = stringResource(R.string.back),
              modifier = Modifier.padding(end = 4.dp)
            )
          }
        }
        TextButton(
          onClick = {
            onRepetitionsClick(
              listOf(repetitionBlock.id),
              repetitionBlock.repetitions
            )
          }
        ) {
          Icon(
            Icons.Rounded.Repeat,
            contentDescription = stringResource(R.string.repetitions),
            modifier = Modifier.padding(end = 4.dp)
          )
          Text(text = (if (fillTime || readOnly) "${currentIndex.value + 1}/" else "") + repetitionBlock.repetitions.toString())
        }
        TextButton(
          onClick = {
            onRecoverClick(
              listOf(repetitionBlock.id),
              repetitionBlock.recoverType,
              repetitionBlock.recoverDuration,
              repetitionBlock.recoverDistance,
              repetitionBlock.recoverDistanceUnit,
              false
            )
          }
        ) {
          Icon(
            Icons.Rounded.Timer,
            contentDescription = stringResource(R.string.recover),
            modifier = Modifier.padding(end = 4.dp)
          )
          Text(
            text = if (repetitionBlock.recoverType == TrainingStep.DurationType.TIME)
              secondsToHhMmSs(repetitionBlock.recoverDuration)
            else "${repetitionBlock.recoverDistance} ${repetitionBlock.recoverDistanceUnit}"
          )
        }
        if (!lastStep) {
          TextButton(
            onClick = {
              onRecoverClick(
                listOf(repetitionBlock.id),
                repetitionBlock.extraRecoverType,
                repetitionBlock.extraRecoverDuration,
                repetitionBlock.extraRecoverDistance,
                repetitionBlock.extraRecoverDistanceUnit,
                true
              )
            }
          ) {
            Icon(
              Icons.Rounded.MoreTime,
              contentDescription = stringResource(R.string.extra_recover),
              modifier = Modifier.padding(end = 4.dp)
            )
            Text(
              text = if (repetitionBlock.extraRecoverType == TrainingStep.DurationType.TIME)
                secondsToHhMmSs(repetitionBlock.extraRecoverDuration)
              else "${repetitionBlock.extraRecoverDistance} ${repetitionBlock.extraRecoverDistanceUnit}"
            )
          }
        }
        if (fillTime || readOnly) {
          TextButton(onClick = { currentIndex.value = minOf(currentIndex.value + 1, repetitionBlock.repetitions - 1) }) {
            Icon(
              Icons.Rounded.ArrowRight,
              contentDescription = stringResource(R.string.forward),
              modifier = Modifier.padding(end = 4.dp)
            )
          }
        }
      }
    }

    if (readOnly) {
      UnmodifiableStepsList(
        trainingSteps = repetitionBlock.stepsInRepetition,
        fillTime = fillTime,
        onTimeFillClick = { descendants, index, step ->
          onTimeFillClick(
            listOf(repetitionBlock.id) + descendants,
            index,
            step
          )
        },
        resultIndex = currentIndex.value,
      )
    } else {
      val state = rememberReorderableLazyListState(
        onMove = { from, to -> onMove(listOf(repetitionBlock.id), from, to) }
      )
      LazyColumn(
        state = state.listState,
        modifier = Modifier
          .reorderable(state)
          .detectReorderAfterLongPress(state),
        userScrollEnabled = false
      ) {
        items(repetitionBlock.stepsInRepetition, { it.id }) { trainingStep ->
          ReorderableItem(state, key = trainingStep.id) {
            when (trainingStep.type) {
              TrainingStep.Type.WARM_UP -> WarmUpCard(
                trainingStep,
                onDeleteClick = { _, trainingStep ->
                  onDeleteClick(
                    listOf(repetitionBlock.id),
                    trainingStep
                  )
                },
                onEditClick = { _, trainingStep ->
                  onEditClick(
                    listOf(repetitionBlock.id),
                    trainingStep
                  )
                }
              )

              TrainingStep.Type.COOL_DOWN -> CoolDownCard(
                trainingStep,
                onDeleteClick = { _, trainingStep ->
                  onDeleteClick(
                    listOf(repetitionBlock.id),
                    trainingStep
                  )
                },
                onEditClick = { _, trainingStep ->
                  onEditClick(
                    listOf(repetitionBlock.id),
                    trainingStep
                  )
                }
              )

              TrainingStep.Type.REPETITION -> RepetitionsCard(
                trainingStep,
                showRecover = trainingStep.id != repetitionBlock.stepsInRepetition.last().id,
                onDeleteClick = { _, trainingStep ->
                  onDeleteClick(
                    listOf(repetitionBlock.id),
                    trainingStep
                  )
                },
                onEditClick = { _, trainingStep ->
                  onEditClick(
                    listOf(repetitionBlock.id),
                    trainingStep
                  )
                }
              )

              TrainingStep.Type.REPETITION_BLOCK -> RepetitionBlockCard(
                trainingStep,
                onDeleteClick = { descendants, trainingStep ->
                  onDeleteClick(
                    listOf(repetitionBlock.id) + descendants,
                    trainingStep
                  )
                },
                onEditClick = { descendants, trainingStep ->
                  onEditClick(
                    listOf(repetitionBlock.id) + descendants,
                    trainingStep
                  )
                },
                onAddClick = { descendants -> onAddClick(listOf(repetitionBlock.id) + descendants) },
                onAddBlockClick = { descendants, repetitions ->
                  onAddBlockClick(
                    listOf(
                      repetitionBlock.id
                    ) + descendants, repetitions
                  )
                },
                onRepetitionsClick = { descendants, repetitions ->
                  onRepetitionsClick(
                    listOf(
                      repetitionBlock.id
                    ) + descendants, repetitions
                  )
                },
                onRecoverClick = { descendants, recoverType, recoverDuration, recoverDistance, recoverDistanceUnit, extraRecover ->
                  onRecoverClick(
                    listOf(repetitionBlock.id) + descendants,
                    recoverType,
                    recoverDuration,
                    recoverDistance,
                    recoverDistanceUnit,
                    extraRecover
                  )
                },
                onMove = { descendants, from, to ->
                  onMove(listOf(repetitionBlock.id) + descendants, from, to)
                },
                lastStep = trainingStep.id == repetitionBlock.stepsInRepetition.last().id,
                level = level + 1
              )
            }
          }
        }
      }
      AddButtons(
        onAddClick = { _ -> onAddClick(listOf(repetitionBlock.id)) },
        onAddBlockClick = { _, repetitions ->
          onAddBlockClick(
            listOf(repetitionBlock.id),
            repetitions
          )
        },
        repetitionsBlockAllowed = level < 2
      )
    }
  }
}

@Composable
fun StepCardHeader(
  modifier: Modifier = Modifier,
  stepType: String,
  onDeleteClick: () -> Unit,
  readOnly: Boolean = false,
  content: @Composable () -> Unit
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(start = if (readOnly) 0.dp else 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (!readOnly) {
        Icon(
          Icons.Rounded.DragIndicator,
          contentDescription = stringResource(R.string.move_step),
        )
      }

      when (
        stepType
      ) {
        TrainingStep.Type.WARM_UP -> {
          Icon(
            Icons.Rounded.DirectionsWalk,
            contentDescription = stringResource(R.string.warm_up),
            modifier = Modifier
              .padding(start = if (readOnly) 16.dp else 8.dp, end = 8.dp)
              .drawBehind {
                drawCircle(
                  color = Color.Green.copy(alpha = 0.4f),
                  radius = 16.dp.toPx()
                )
              },
            tint = Color.White
          )
        }

        TrainingStep.Type.COOL_DOWN -> {
          Icon(
            Icons.Rounded.DirectionsWalk,
            contentDescription = stringResource(R.string.cool_down),
            modifier = Modifier
              .padding(start = if (readOnly) 16.dp else 8.dp, end = 8.dp)
              .drawBehind {
                drawCircle(
                  color = Color.Blue.copy(alpha = 0.4f),
                  radius = 16.dp.toPx()
                )
              },
            tint = Color.White
          )
        }

        TrainingStep.Type.REPETITION -> {
          Icon(
            Icons.Rounded.DirectionsRun,
            contentDescription = stringResource(R.string.repetition),
            modifier = Modifier
              .padding(start = if (readOnly) 16.dp else 8.dp, end = 8.dp)
              .drawBehind {
                drawCircle(
                  color = Color.Red.copy(alpha = 0.4f),
                  radius = 16.dp.toPx()
                )
              },
            tint = Color.White
          )
        }
      }

      content()
    }

    if (!readOnly) {
      val openDeleteDialog = rememberSaveable { mutableStateOf(false) }

      IconButton(
        modifier = Modifier.padding(8.dp),
        onClick = { openDeleteDialog.value = true }
      ) {
        Icon(
          Icons.Rounded.Delete,
          contentDescription = stringResource(R.string.delete_repetition)
        )
      }

      if (openDeleteDialog.value) {
        DeleteDialog(
          onDeleteClick = {
            openDeleteDialog.value = false
            onDeleteClick()
          },
          onDismissRequest = { openDeleteDialog.value = false }
        )
      }
    }
  }
}

@Composable
fun AddButtons(
  onAddClick: (List<String>) -> Unit,
  onAddBlockClick: (List<String>, Int) -> Unit,
  repetitionsBlockAllowed: Boolean
) {
  BoxWithConstraints {
    val availableWidth = with(LocalDensity.current) { constraints.maxWidth.toDp() }
    val isEnoughSpaceForIcon = availableWidth > 320.dp
    val isEnoughSpaceForText = availableWidth > 260.dp

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      if (isEnoughSpaceForText) {
        Button(
          onClick = { onAddClick(listOf()) }
        ) {
          Text(text = "Add repetition")
        }
      } else {
        FilledIconButton(
          onClick = { onAddClick(listOf()) }
        ) {
          Icon(
            Icons.Rounded.Add,
            contentDescription = stringResource(R.string.add_repetition)
          )
        }
      }
      if (repetitionsBlockAllowed) {
        FilledTonalIconButton(
          onClick = { onAddBlockClick(listOf(), 3) }
        ) {
          Text(text = "3x")
        }
        FilledTonalIconButton(
          onClick = { onAddBlockClick(listOf(), 5) }
        ) {
          Text(text = "5x")
        }
        if (isEnoughSpaceForIcon) {
          FilledTonalIconButton(
            onClick = { onAddBlockClick(listOf(), 10) }
          ) {
            Text(text = "10x")
          }
        }
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
      TextButton(
        onClick = onDeleteClick
      ) {
        Text(text = stringResource(R.string.delete))
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismissRequest
      ) {
        Text(text = stringResource(R.string.cancel))
      }
    }
  )
}

@Composable
fun UnmodifiableStepsList(
  trainingSteps: List<TrainingStep>,
  readOnly: Boolean = true,
  fillTime: Boolean = false,
  resultIndex: Int = 0,
  onTimeFillClick: (List<String>, Int, TrainingStep) -> Unit = { _, _, _ -> }
) {
  LazyColumn(
    userScrollEnabled = false
  ) {
    items(trainingSteps, { it.id }) { trainingStep ->
      when (trainingStep.type) {
        TrainingStep.Type.WARM_UP -> WarmUpCard(
          trainingStep = trainingStep,
          readOnly = readOnly
        )

        TrainingStep.Type.COOL_DOWN -> CoolDownCard(
          trainingStep = trainingStep,
          readOnly = readOnly
        )

        TrainingStep.Type.REPETITION -> RepetitionsCard(
          trainingStep = trainingStep,
          showRecover = trainingStep.id != trainingSteps.last().id,
          readOnly = readOnly,
          fillTime = fillTime,
          resultIndex = resultIndex,
          onTimeFillClick = { hierarchy, step -> onTimeFillClick(hierarchy, resultIndex, step) }
        )

        TrainingStep.Type.REPETITION_BLOCK -> RepetitionBlockCard(
          repetitionBlock = trainingStep,
          readOnly = readOnly,
          lastStep = trainingStep.id == trainingSteps.last().id,
          fillTime = fillTime,
          onTimeFillClick = { hierarchy, index, step -> onTimeFillClick(hierarchy, index, step) }
        )
      }
    }
  }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TrainingStepsListBox(
  training: Training,
  filling: Boolean = false,
  openScreen: (String) -> Unit
) {
  val tree = training.calculateTree()

  OutlinedCard(
    modifier = Modifier
      .fieldModifier()
      .fillMaxWidth()
      .height(max(32.dp + 78.dp * (tree.first) + 70.dp * (tree.second), 128.dp)),
  ) {
    Scaffold(
      floatingActionButton = {
        Row(
          modifier = Modifier
            .fillMaxHeight()
            .padding(end = 8.dp, top = 40.dp),
          verticalAlignment = Alignment.Top
        ) {
          FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            onClick = {
              if (filling) {
                openScreen("$FILL_REPETITIONS_SCREEN?$TRAINING_ID={${training.id}}")
              } else {
                // TODO save training
                openScreen("$EDIT_REPETITIONS_SCREEN?$TRAINING_ID={${training.id}}")
              }
            },
          ) {
            if (filling)
              Icon(
                Icons.Rounded.InsertChartOutlined,
                contentDescription = stringResource(R.string.fill_training)
              )
            else Icon(
              Icons.Rounded.Edit,
              contentDescription = stringResource(R.string.edit_repetitions)
            )
          }
        }
      },
      floatingActionButtonPosition = FabPosition.End
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        UnmodifiableStepsList(trainingSteps = training.trainingSteps)
      }
    }
  }
}



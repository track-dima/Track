package it.polimi.dima.track.data

import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.TrainingStep
import it.polimi.dima.track.model.Type
import java.util.Calendar

private val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 2) }

private val trainingStep = TrainingStep(id = "1", type = TrainingStep.Type.WARM_UP, duration = 600)

private val trainings = (1..3).map {
  Training(
    id = it.toString(),
    title = "Training $it",
    description = "Description $it",
    notes = "Notes $it",
    dueDate = calendar.apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
    dueDateString = "Test date",
    type = Type.Track.toString(),
    trainingSteps = listOf(trainingStep),
  )
}

val emptyTraining = Training(
  id = (trainings.size + 1).toString(),
)

val mockedTrainings: List<Training> = trainings + emptyTraining

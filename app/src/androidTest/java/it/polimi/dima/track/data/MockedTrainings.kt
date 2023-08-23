package it.polimi.dima.track.data

import it.polimi.dima.track.model.Training
import java.util.Calendar

private val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 2) }

val mockedTrainings: List<Training> = (1..10).map {
  Training(
    id = it.toString(),
    title = "Training $it",
    description = "Description $it",
    notes = "Notes $it",
    dueDate = calendar.apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
  )
}

val mockedTraining = mockedTrainings[0]

val mockedEmptyTrainings: List<Training> = listOf()
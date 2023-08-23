package it.polimi.dima.track.data

import it.polimi.dima.track.model.Training

val mockedTrainings: List<Training> = (1..10).map {
  Training(
    id = it.toString(),
    title = "Training $it",
    description = "Description $it",
    notes = "Notes $it",
  )
}

val mockedTraining = mockedTrainings[0]

val mockedEmptyTrainings: List<Training> = listOf()
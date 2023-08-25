package it.polimi.dima.track.model.service.fitbit

import it.polimi.dima.track.model.Training

interface FitbitService {
  suspend fun getActivitiesByTraining(training: Training): List<FitbitActivity>
}
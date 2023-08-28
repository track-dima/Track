package it.polimi.dima.track.model.service.fitbit

import it.polimi.dima.track.model.Training

interface FitbitService {
  var token: FitbitOAuthToken
  suspend fun getActivitiesByTraining(training: Training): List<FitbitActivity>
}
package it.polimi.dima.track.model.service

import it.polimi.dima.track.model.PersonalBest
import it.polimi.dima.track.model.Training
import kotlinx.coroutines.flow.Flow

interface StorageService {
  val trainings: Flow<List<Training>>
  val personalBests: Flow<List<PersonalBest>>
  suspend fun searchTrainings(query: String): List<Training>
  suspend fun getTraining(trainingId: String): Training?
  suspend fun saveTraining(training: Training): String
  suspend fun updateTraining(training: Training)
  suspend fun duplicateTraining(training: Training): String
  suspend fun deleteTraining(trainingId: String)
  suspend fun deleteAllForUser(userId: String)

  suspend fun getPersonalBestFromDistance(distance: Int): PersonalBest?
  suspend fun getPersonalBestFromDuration(duration: Int): PersonalBest?
  suspend fun savePersonalBest(personalBest: PersonalBest): String
  suspend fun updatePersonalBest(personalBest: PersonalBest)

}

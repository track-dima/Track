package it.polimi.dima.track.model.service.storage

import it.polimi.dima.track.model.Training
import kotlinx.coroutines.flow.Flow

interface TrainingStorageService {
  val trainings: Flow<List<Training>>
  suspend fun searchTrainings(query: String): List<Training>
  suspend fun getTraining(trainingId: String): Training?
  suspend fun saveTraining(training: Training): String
  suspend fun updateTraining(training: Training)
  suspend fun duplicateTraining(training: Training): String
  suspend fun deleteTraining(trainingId: String)
  suspend fun updatePersonalBestFlag(trainingId: String, flag: Boolean)
}

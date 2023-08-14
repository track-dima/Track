package it.polimi.dima.track.model.service

import it.polimi.dima.track.model.Training
import kotlinx.coroutines.flow.Flow

interface StorageService {
  val trainings: Flow<List<Training>>

  suspend fun getFavoriteTrainings(): List<Training>
  suspend fun searchTrainings(query: String): List<Training>
  suspend fun getTraining(trainingId: String): Training?
  suspend fun save(training: Training): String
  suspend fun update(training: Training)
  suspend fun duplicate(training: Training): String
  suspend fun delete(trainingId: String)
  suspend fun deleteAllForUser(userId: String)
}

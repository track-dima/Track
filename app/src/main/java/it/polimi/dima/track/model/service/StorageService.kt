package it.polimi.dima.track.model.service

import it.polimi.dima.track.model.PersonalBest
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.User
import kotlinx.coroutines.flow.Flow

interface StorageService {
  val trainings: Flow<List<Training>>
  val user: Flow<User>
  val personalBests: Flow<List<PersonalBest>>
  suspend fun searchTrainings(query: String): List<Training>
  suspend fun getTraining(trainingId: String): Training?
  suspend fun saveTraining(training: Training): String
  suspend fun updateTraining(training: Training)
  suspend fun duplicateTraining(training: Training): String
  suspend fun deleteTraining(trainingId: String)
  suspend fun deleteAllForUser(userId: String)
  suspend fun updatePersonalBestFlag(trainingId: String, flag: Boolean)

  suspend fun getGlobalPersonalBestFromDistance(distance: Int): PersonalBest?
  suspend fun getGlobalPersonalBestFromDuration(duration: Int): PersonalBest?
  suspend fun getSecondGlobalPersonalBestFromDistance(distance: Int): PersonalBest?
  suspend fun getSecondGlobalPersonalBestFromDuration(duration: Int): PersonalBest?
  suspend fun getPersonalBestFromDistanceAndTraining(distance: Int, trainingId: String): PersonalBest?
  suspend fun getPersonalBestFromDurationAndTraining(duration: Int, trainingId: String): PersonalBest?
  suspend fun existsGlobalPersonalBestWithTrainingId(trainingId: String): Boolean
  suspend fun savePersonalBest(personalBest: PersonalBest): String
  suspend fun updatePersonalBest(personalBest: PersonalBest)
  suspend fun updateUserName(newName: String)
  suspend fun updateUserSpecialty(newSpecialty: String)
}

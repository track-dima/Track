package it.polimi.dima.track.model.service.storage

import it.polimi.dima.track.model.PersonalBest
import kotlinx.coroutines.flow.Flow

interface PersonalBestStorageService {
  val personalBests: Flow<List<PersonalBest>>
  suspend fun getGlobalPersonalBestFromDistance(distance: Int): PersonalBest?
  suspend fun getGlobalPersonalBestFromDuration(duration: Int): PersonalBest?
  suspend fun getSecondGlobalPersonalBestFromDistance(distance: Int): PersonalBest?
  suspend fun getSecondGlobalPersonalBestFromDuration(duration: Int): PersonalBest?
  suspend fun getPersonalBestFromDistanceAndTraining(distance: Int, trainingId: String): PersonalBest?
  suspend fun getPersonalBestFromDurationAndTraining(duration: Int, trainingId: String): PersonalBest?
  suspend fun existsGlobalPersonalBestWithTrainingId(trainingId: String): Boolean
  suspend fun savePersonalBest(personalBest: PersonalBest): String
  suspend fun updatePersonalBest(personalBest: PersonalBest)
}

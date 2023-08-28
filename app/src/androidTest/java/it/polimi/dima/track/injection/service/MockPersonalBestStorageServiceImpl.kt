package it.polimi.dima.track.injection.service

import com.google.firebase.firestore.FirebaseFirestore
import it.polimi.dima.track.model.PersonalBest
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.storage.PersonalBestStorageService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class MockPersonalBestStorageServiceImpl
@Inject
@Suppress("unused")
constructor(private val firestore: FirebaseFirestore, private val auth: AccountService) :
  PersonalBestStorageService {

  override val personalBests: Flow<List<PersonalBest>>
    get() = flowOf(listOf())

  override suspend fun getGlobalPersonalBestFromDistance(distance: Int): PersonalBest? = null

  override suspend fun getGlobalPersonalBestFromDuration(duration: Int): PersonalBest? = null

  override suspend fun getSecondGlobalPersonalBestFromDistance(distance: Int): PersonalBest? = null

  override suspend fun getSecondGlobalPersonalBestFromDuration(duration: Int): PersonalBest? = null

  override suspend fun getPersonalBestFromDistanceAndTraining(distance: Int, trainingId: String): PersonalBest? = null

  override suspend fun getPersonalBestFromDurationAndTraining(duration: Int, trainingId: String): PersonalBest? = null

  override suspend fun existsGlobalPersonalBestWithTrainingId(trainingId: String): Boolean = false

  override suspend fun savePersonalBest(personalBest: PersonalBest): String = ""

  override suspend fun updatePersonalBest(personalBest: PersonalBest): Unit = Unit
}

package it.polimi.dima.track.model.service.impl.storage

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObjects
import it.polimi.dima.track.common.ext.paceToSeconds
import it.polimi.dima.track.common.ext.timeToSeconds
import it.polimi.dima.track.model.PersonalBest
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.storage.PersonalBestStorageService
import it.polimi.dima.track.model.service.trace
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PersonalBestStorageServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore, private val auth: AccountService) :
  PersonalBestStorageService {

  @OptIn(ExperimentalCoroutinesApi::class)
  override val personalBests: Flow<List<PersonalBest>>
    get() =
      auth.currentUser.flatMapLatest { user ->
        currentPersonalBestCollection(user.id).snapshots().map { snapshot -> snapshot.toObjects() }
      }

  override suspend fun getGlobalPersonalBestFromDistance(distance: Int): PersonalBest? =
    currentPersonalBestCollection(auth.currentUserId)
      .whereEqualTo("distance", distance)
      .whereEqualTo("globalPersonalBest", true)
      .get()
      .await()
      .toObjects<PersonalBest>()
      .firstOrNull()

  override suspend fun getGlobalPersonalBestFromDuration(duration: Int): PersonalBest? =
    currentPersonalBestCollection(auth.currentUserId)
      .whereEqualTo("duration", duration)
      .whereEqualTo("globalPersonalBest", true)
      .get()
      .await()
      .toObjects<PersonalBest>()
      .firstOrNull()

  override suspend fun getSecondGlobalPersonalBestFromDistance(distance: Int): PersonalBest? =
    currentPersonalBestCollection(auth.currentUserId)
      .whereEqualTo("distance", distance)
      .whereEqualTo("globalPersonalBest", false)
      .get()
      .await()
      .toObjects<PersonalBest>()
      .minByOrNull { it.result.timeToSeconds() }

  override suspend fun getSecondGlobalPersonalBestFromDuration(duration: Int): PersonalBest? =
    currentPersonalBestCollection(auth.currentUserId)
      .whereEqualTo("duration", duration)
      .whereEqualTo("globalPersonalBest", false)
      .get()
      .await()
      .toObjects<PersonalBest>()
      .minByOrNull { it.result.paceToSeconds() }

  override suspend fun getPersonalBestFromDistanceAndTraining(distance: Int, trainingId: String): PersonalBest? =
    currentPersonalBestCollection(auth.currentUserId)
      .whereEqualTo("distance", distance)
      .whereEqualTo("trainingId", trainingId)
      .get()
      .await()
      .toObjects<PersonalBest>()
      .firstOrNull()

  override suspend fun getPersonalBestFromDurationAndTraining(duration: Int, trainingId: String): PersonalBest? =
    currentPersonalBestCollection(auth.currentUserId)
      .whereEqualTo("duration", duration)
      .whereEqualTo("trainingId", trainingId)
      .get()
      .await()
      .toObjects<PersonalBest>()
      .firstOrNull()

  override suspend fun existsGlobalPersonalBestWithTrainingId(trainingId: String): Boolean =
    currentPersonalBestCollection(auth.currentUserId)
      .whereEqualTo("trainingId", trainingId)
      .whereEqualTo("globalPersonalBest", true)
      .get()
      .await()
      .toObjects<PersonalBest>()
      .isNotEmpty()

  override suspend fun savePersonalBest(personalBest: PersonalBest): String =
    trace(SAVE_PERSONAL_BEST_TRACE) {
      currentPersonalBestCollection(auth.currentUserId).add(personalBest).await().id
    }

  override suspend fun updatePersonalBest(personalBest: PersonalBest): Unit =
    trace(UPDATE_PERSONAL_BEST_TRACE) {
      currentPersonalBestCollection(auth.currentUserId).document(personalBest.id).set(personalBest)
        .await()
    }

 private fun currentPersonalBestCollection(uid: String): CollectionReference =
    firestore.collection(USER_COLLECTION).document(uid).collection(PERSONAL_BEST_COLLECTION)

  companion object {
    private const val USER_COLLECTION = "users"
    private const val PERSONAL_BEST_COLLECTION = "personalBests"
    private const val SAVE_PERSONAL_BEST_TRACE = "savePersonalBest"
    private const val UPDATE_PERSONAL_BEST_TRACE = "updatePersonalBest"
  }
}

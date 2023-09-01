package it.polimi.dima.track.model.service.impl.storage

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TrainingStorageServiceImpl @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val auth: AccountService
) : TrainingStorageService {

  @OptIn(ExperimentalCoroutinesApi::class)
  override val trainings: Flow<List<Training>>
    get() =
      auth.currentUser.flatMapLatest { user ->
        currentTrainingCollection(user.id).snapshots().map { snapshot -> snapshot.toObjects() }
      }

  override suspend fun searchTrainings(query: String): List<Training> =
    currentTrainingCollection(auth.currentUserId)
      .whereArrayContainsAny("searchable", query.split(" ").map { it.lowercase() })
      .get()
      .await()
      .toObjects()

  override suspend fun getTraining(trainingId: String): Training? =
    currentTrainingCollection(auth.currentUserId)
      .document(trainingId)
      .get()
      .await()
      .toObject()

  override suspend fun saveTraining(training: Training): String =
    currentTrainingCollection(auth.currentUserId)
      .add(training)
      .await()
      .id

  override suspend fun updateTraining(training: Training) {
    currentTrainingCollection(auth.currentUserId)
      .document(training.id)
      .set(training)
      .await()
  }

  override suspend fun updatePersonalBestFlag(trainingId: String, flag: Boolean) {
    currentTrainingCollection(auth.currentUserId)
      .document(trainingId)
      .update("personalBest", flag)
      .await()
  }

  override suspend fun duplicateTraining(training: Training): String =
    currentTrainingCollection(auth.currentUserId)
      .add(training.copy(id = ""))
      .await()
      .id


  override suspend fun deleteTraining(trainingId: String) {
    currentTrainingCollection(auth.currentUserId)
      .document(trainingId)
      .delete()
      .await()
  }

  private fun currentTrainingCollection(uid: String): CollectionReference =
    firestore
      .collection(USER_COLLECTION)
      .document(uid)
      .collection(TRAINING_COLLECTION)

  companion object {
    private const val USER_COLLECTION = "users"
    private const val TRAINING_COLLECTION = "trainings"
  }
}

package it.polimi.dima.track.model.service.impl.storage

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import it.polimi.dima.track.model.service.trace
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TrainingStorageServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore, private val auth: AccountService) :
  TrainingStorageService {

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
    currentTrainingCollection(auth.currentUserId).document(trainingId).get().await().toObject()

  override suspend fun saveTraining(training: Training): String =
    trace(SAVE_TRAINING_TRACE) {
      currentTrainingCollection(auth.currentUserId).add(training).await().id
    }

  override suspend fun updateTraining(training: Training): Unit =
    trace(UPDATE_TRAINING_TRACE) {
      currentTrainingCollection(auth.currentUserId).document(training.id).set(training).await()
    }

  override suspend fun updatePersonalBestFlag(trainingId: String, flag: Boolean): Unit =
    trace(UPDATE_PERSONAL_BEST_FLAG_TRACE) {
      currentTrainingCollection(auth.currentUserId).document(trainingId)
        .update("personalBest", flag).await()
    }

  override suspend fun duplicateTraining(training: Training): String =
    trace(DUPLICATE_TRAINING_TRACE) {
      currentTrainingCollection(auth.currentUserId).add(training.copy(id = "")).await().id
    }

  override suspend fun deleteTraining(trainingId: String): Unit =
    // NOT USED
    trace(DELETE_TRAINING_TRACE) {
      currentTrainingCollection(auth.currentUserId).document(trainingId).delete().await()
    }

  // TODO: It's not recommended to delete on the client:
  // https://firebase.google.com/docs/firestore/manage-data/delete-data#kotlin+ktx_2
  override suspend fun deleteAllForUser(userId: String) {
    val matchingTasks = currentTrainingCollection(userId).get().await()
    matchingTasks.map { it.reference.delete().asDeferred() }.awaitAll()
  }

  private fun currentTrainingCollection(uid: String): CollectionReference =
    firestore.collection(USER_COLLECTION).document(uid).collection(TRAINING_COLLECTION)

  companion object {
    private const val USER_COLLECTION = "users"
    private const val TRAINING_COLLECTION = "trainings"
    private const val SAVE_TRAINING_TRACE = "saveTraining"
    private const val DELETE_TRAINING_TRACE = "deleteTraining"
    private const val DUPLICATE_TRAINING_TRACE = "duplicateTraining"
    private const val UPDATE_TRAINING_TRACE = "updateTraining"
    private const val UPDATE_PERSONAL_BEST_FLAG_TRACE = "updatePersonalBestFlag"
  }
}

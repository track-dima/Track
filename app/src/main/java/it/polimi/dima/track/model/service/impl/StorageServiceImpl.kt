package it.polimi.dima.track.model.service.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.StorageService
import it.polimi.dima.track.model.service.trace
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await

class StorageServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore, private val auth: AccountService) :
  StorageService {

  @OptIn(ExperimentalCoroutinesApi::class)
  override val trainings: Flow<List<Training>>
    get() =
      auth.currentUser.flatMapLatest { user ->
        currentCollection(user.id).snapshots().map { snapshot -> snapshot.toObjects() }
      }

  override suspend fun getTraining(trainingId: String): Training? =
    currentCollection(auth.currentUserId).document(trainingId).get().await().toObject()

  override suspend fun save(training: Training): String =
    trace(SAVE_TRAINING_TRACE) {
      currentCollection(auth.currentUserId).add(training).await().id
    }

  override suspend fun update(training: Training): Unit =
    trace(UPDATE_TRAINING_TRACE) {
      currentCollection(auth.currentUserId).document(training.id).set(training).await()
    }

  override suspend fun duplicate(training: Training): String =
    trace(DUPLICATE_TRAINING_TRACE) {
      currentCollection(auth.currentUserId).add(training.copy(id = "")).await().id
    }

  override suspend fun delete(trainingId: String): Unit =
    trace(DELETE_TRAINING_TRACE) {
      currentCollection(auth.currentUserId).document(trainingId).delete().await()
    }

  // TODO: It's not recommended to delete on the client:
  // https://firebase.google.com/docs/firestore/manage-data/delete-data#kotlin+ktx_2
  override suspend fun deleteAllForUser(userId: String) {
    val matchingTasks = currentCollection(userId).get().await()
    matchingTasks.map { it.reference.delete().asDeferred() }.awaitAll()
  }

  private fun currentCollection(uid: String): CollectionReference =
    firestore.collection(USER_COLLECTION).document(uid).collection(TRAINING_COLLECTION)

  companion object {
    private const val USER_COLLECTION = "users"
    private const val TRAINING_COLLECTION = "trainings"
    private const val SAVE_TRAINING_TRACE = "saveTraining"
    private const val DELETE_TRAINING_TRACE = "deleteTraining"
    private const val DUPLICATE_TRAINING_TRACE = "duplicateTraining"
    private const val UPDATE_TRAINING_TRACE = "updateTraining"
  }
}

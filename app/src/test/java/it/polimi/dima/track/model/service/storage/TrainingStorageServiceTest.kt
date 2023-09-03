package it.polimi.dima.track.model.service.storage

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import io.mockk.every
import io.mockk.mockk
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.impl.storage.TrainingStorageServiceImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executor

class TrainingStorageServiceTest {
  private lateinit var trainingStorageService: TrainingStorageService
  private lateinit var firebaseFirestore: FirebaseFirestore
  private lateinit var collectionReference: CollectionReference
  private lateinit var documentReference: DocumentReference
  private lateinit var documentSnapshot: DocumentSnapshot

  @Before
  fun setUp() {
    firebaseFirestore = mockk()
    collectionReference = mockk()
    documentReference = mockk()
    documentSnapshot = mockk()

    every { firebaseFirestore.collection(any()) } returns collectionReference
    every { collectionReference.document(any()) } returns documentReference
    every { documentReference.collection(any()) } returns collectionReference
    every { documentReference.id } returns "trainingId"

    val accountService: AccountService = mockk()
    every { accountService.currentUserId } returns "userId"

    trainingStorageService = TrainingStorageServiceImpl(firebaseFirestore, accountService)
  }

  @Test
  fun searchTrainings() = runTest {
    val query: Query = mockk()
    val queryTask: Task<QuerySnapshot> = mockk()
    val querySnapshot: QuerySnapshot = mockk()

    every { collectionReference.whereArrayContainsAny(any<String>(), any()) } returns query
    every { query.get() } returns queryTask

    every { queryTask.isComplete } returns true
    every { queryTask.isCanceled } returns false
    every { queryTask.exception } returns null
    every { queryTask.result } returns querySnapshot

    every { querySnapshot.toObjects<Training>() } returns listOf(Training(
      title = "Test training",
    ))

    val trainings = trainingStorageService.searchTrainings("test training")

    assertTrue(trainings.isNotEmpty())
  }

  @Test
  fun getTraining() = runTest {
    val getTask: Task<DocumentSnapshot> = mockk()

    every { documentReference.get() } returns getTask

    every { getTask.isComplete } returns true
    every { getTask.isCanceled } returns false
    every { getTask.exception } returns null
    every { getTask.result } returns documentSnapshot

    every { documentSnapshot.toObject<Training>() } returns Training()

    trainingStorageService.getTraining("trainingId")
  }

  @Test
  fun saveTraining() = runTest {
    val documentReferenceTask: Task<DocumentReference> = mockk()
    every { collectionReference.add(any()) } returns documentReferenceTask

    every { documentReferenceTask.isComplete } returns true
    every { documentReferenceTask.isCanceled } returns false
    every { documentReferenceTask.exception } returns null
    every { documentReferenceTask.result } returns documentReference
    every {
      documentReferenceTask.addOnCompleteListener(
        any<Executor>(),
        any()
      )
    } returns documentReferenceTask

    trainingStorageService.saveTraining(Training())
  }

  @Test
  fun updateTraining() = runTest {
    val updateTask: Task<Void> = mockk()

    every { documentReference.update(any<String>(), any()) } returns updateTask

    every { updateTask.isComplete } returns true
    every { updateTask.isCanceled } returns false
    every { updateTask.exception } returns null
    every { updateTask.result } returns null

    every { documentReference.set(any()) } returns updateTask

    trainingStorageService.updateTraining(Training())
  }

  @Test
  fun duplicateTraining() = runTest {
    val task: Task<DocumentReference> = mockk()

    every { collectionReference.add(any()) } returns task

    every { task.isComplete } returns true
    every { task.isCanceled } returns false
    every { task.exception } returns null
    every { task.result } returns documentReference

    trainingStorageService.duplicateTraining(Training())
  }

  @Test
  fun deleteTraining() = runTest {
    val task: Task<Void> = mockk()

    every { documentReference.delete() } returns task

    every { task.isComplete } returns true
    every { task.isCanceled } returns false
    every { task.exception } returns null
    every { task.result } returns null

    trainingStorageService.deleteTraining("trainingId")
  }

  @Test
  fun updatePersonalBestFlag() = runTest {
    val updateTask: Task<Void> = mockk()

    every { documentReference.update(any<String>(), any()) } returns updateTask

    every { updateTask.isComplete } returns true
    every { updateTask.isCanceled } returns false
    every { updateTask.exception } returns null
    every { updateTask.result } returns null

    trainingStorageService.updatePersonalBestFlag("trainingId", true)
  }
}
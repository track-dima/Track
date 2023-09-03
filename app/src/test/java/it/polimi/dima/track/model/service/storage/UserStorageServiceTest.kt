package it.polimi.dima.track.model.service.storage

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.fitbit.FitbitOAuthToken
import it.polimi.dima.track.model.service.impl.storage.UserStorageServiceImpl
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UserStorageServiceTest {
  private lateinit var userStorageService: UserStorageService

  private lateinit var firebaseFirestore: FirebaseFirestore
  private lateinit var collectionReference: CollectionReference
  private lateinit var documentReference: DocumentReference

  @Before
  fun setUp() {
    firebaseFirestore = mockk()
    collectionReference = mockk()
    documentReference = mockk()
    val task: Task<Void> = mockk()

    every { firebaseFirestore.collection(any()) } returns collectionReference
    every { collectionReference.document(any()) } returns documentReference
    every { documentReference.update(any<String>(), any()) } returns task
    every { task.isComplete } returns true
    every { task.isCanceled } returns false
    every { task.exception } returns null
    every { task.result } returns null

    val accountService: AccountService = mockk()
    every { accountService.currentUserId } returns "userId"

    userStorageService = UserStorageServiceImpl(firebaseFirestore, accountService)
  }

  @Test
  fun updateUserName() = runTest {
    val newName = "New name"
    userStorageService.updateUserName(newName)
    verifyOrder {
      firebaseFirestore.collection("users")
      collectionReference.document(any())
      documentReference.update("name", newName)
    }
  }

  @Test
  fun updateUserName_EmptyName() = runTest {
    userStorageService.updateUserName("")
    verifyOrder {
      firebaseFirestore.collection("users")
      collectionReference.document(any())
      documentReference.update("name", "")
    }
  }

  @Test
  fun updateUserSpecialty() = runTest {
    val newSpecialty = "New specialty"
    userStorageService.updateUserSpecialty(newSpecialty)
    verifyOrder {
      firebaseFirestore.collection("users")
      collectionReference.document(any())
      documentReference.update("specialty", newSpecialty)
    }
  }

  @Test
  fun updateUserSpecialty_EmptySpecialty() = runTest {
    userStorageService.updateUserSpecialty("")
    verifyOrder {
      firebaseFirestore.collection("users")
      collectionReference.document(any())
      documentReference.update("specialty", "")
    }
  }

  @Test
  fun updateFitbitToken() = runTest {
    val fitbitOAuthToken = FitbitOAuthToken()
    userStorageService.updateFitbitToken(fitbitOAuthToken)
    verifyOrder {
      firebaseFirestore.collection("users")
      collectionReference.document(any())
      documentReference.update("fitbitToken", fitbitOAuthToken)
    }
  }

  @Test
  fun updateFitbitToken_NullToken() = runTest {
    userStorageService.updateFitbitToken(null)
    verifyOrder {
      firebaseFirestore.collection("users")
      collectionReference.document(any())
      documentReference.update("fitbitToken", null)
    }
  }
}
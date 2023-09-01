package it.polimi.dima.track.model.service.storage

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.fitbit.FitbitOAuthToken
import it.polimi.dima.track.model.service.impl.storage.UserStorageServiceImpl
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UserStorageServiceTest {
  private lateinit var userStorageService: UserStorageService

  @Before
  fun setUp() {
    val firebaseFirestore: FirebaseFirestore = mockk()
    val collectionReference: CollectionReference = mockk()
    val documentReference: DocumentReference = mockk()
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
    userStorageService.updateUserName("Test Name")
  }

  @Test
  fun updateUserName_EmptyName() = runTest {
    userStorageService.updateUserName("")
  }

  @Test
  fun updateUserSpecialty() = runTest {
    userStorageService.updateUserSpecialty("New specialty")
  }

  @Test
  fun updateUserSpecialty_EmptySpecialty() = runTest {
    userStorageService.updateUserSpecialty("")
  }

  @Test
  fun updateFitbitToken() = runTest {
    userStorageService.updateFitbitToken(FitbitOAuthToken())
  }

  @Test
  fun updateFitbitToken_NullToken() = runTest {
    userStorageService.updateFitbitToken(null)
  }
}
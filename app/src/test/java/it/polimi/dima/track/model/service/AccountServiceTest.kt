package it.polimi.dima.track.model.service

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.mockk
import it.polimi.dima.track.model.service.impl.AccountServiceImpl
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AccountServiceTest {
  private lateinit var accountService: AccountService

  private lateinit var firebaseAuth: FirebaseAuth

  @Before
  fun setUp() {
    firebaseAuth = mockk()
    accountService = AccountServiceImpl(firebaseAuth)
  }

  @Test
  fun authenticate() = runTest {
    val task: Task<AuthResult> = mockk()
    val result: AuthResult = mockk()

    every { firebaseAuth.signInWithEmailAndPassword(any(), any()) } returns task

    every { task.isComplete } returns true
    every { task.isCanceled } returns false
    every { task.exception } returns null
    every { task.result } returns result

    accountService.authenticate("email@example.com", "P4ssw0rd")
  }

  @Test
  fun sendRecoveryEmail() = runTest {
    val task: Task<Void> = mockk()

    every { firebaseAuth.sendPasswordResetEmail(any()) } returns task

    every { task.isComplete } returns true
    every { task.isCanceled } returns false
    every { task.exception } returns null
    every { task.result } returns null

    accountService.sendRecoveryEmail("email@example.com")
  }

  @Test
  fun createAnonymousAccount() = runTest {
    val task: Task<AuthResult> = mockk()
    val result: AuthResult = mockk()

    every { firebaseAuth.signInAnonymously() } returns task

    every { task.isComplete } returns true
    every { task.isCanceled } returns false
    every { task.exception } returns null
    every { task.result } returns result

    accountService.createAnonymousAccount()
  }
}
package it.polimi.dima.track.model.service.impl.storage

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import it.polimi.dima.track.model.User
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.fitbit.FitbitOAuthToken
import it.polimi.dima.track.model.service.storage.UserStorageService
import it.polimi.dima.track.model.service.trace
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserStorageServiceImpl @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val auth: AccountService,
) : UserStorageService {
  @OptIn(ExperimentalCoroutinesApi::class)
  override val user: Flow<User>
    get() =
      auth.currentUser.flatMapLatest { user ->
        if (user.isAnonymous) {
          auth.currentUser
        } else {
          firestore.collection(USER_COLLECTION).document(user.id).snapshots()
            .map { snapshot ->
              val userObject = snapshot.toObject<User>()!!
              userObject.copy(isAnonymous = false)
            }
        }
      }

  override suspend fun updateUserName(newName: String) {
    trace(UPDATE_USER_NAME_TRACE) {
      firestore.collection(USER_COLLECTION).document(auth.currentUserId).update("name", newName)
        .await()
    }
  }

  override suspend fun updateUserSpecialty(newSpecialty: String) {
    trace(UPDATE_USER_SPECIALTY_TRACE) {
      firestore.collection(USER_COLLECTION).document(auth.currentUserId)
        .update("specialty", newSpecialty).await()
    }
  }

  override suspend fun updateFitbitToken(token: FitbitOAuthToken?) {
    firestore
      .collection(USER_COLLECTION)
      .document(auth.currentUserId)
      .update("fitbitToken", token)
      .await()
  }

  companion object {
    private const val USER_COLLECTION = "users"
    private const val UPDATE_USER_NAME_TRACE = "updateUserName"
    private const val UPDATE_USER_SPECIALTY_TRACE = "updateUserSpecialty"
    private const val UPDATE_FITBIT_TOKEN_TRACE = "updateFitbitToken"
  }
}

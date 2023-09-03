package it.polimi.dima.track.model.service.storage

import it.polimi.dima.track.model.User
import it.polimi.dima.track.model.service.fitbit.FitbitOAuthToken
import kotlinx.coroutines.flow.Flow

interface UserStorageService {
  val user: Flow<User>
  suspend fun updateUserName(newName: String)
  suspend fun updateUserSpecialty(newSpecialty: String)
  suspend fun updateFitbitToken(token: FitbitOAuthToken?)
}

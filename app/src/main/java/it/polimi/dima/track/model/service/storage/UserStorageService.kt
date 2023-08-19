package it.polimi.dima.track.model.service.storage

import it.polimi.dima.track.model.User
import kotlinx.coroutines.flow.Flow

interface UserStorageService {
  val user: Flow<User>
  suspend fun updateUserName(newName: String)
  suspend fun updateUserSpecialty(newSpecialty: String)
}

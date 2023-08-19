package it.polimi.dima.track.screens.profile

import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.SETTINGS_SCREEN
import it.polimi.dima.track.model.service.ConfigurationService
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.storage.UserStorageService
import it.polimi.dima.track.screens.TrackViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
  logService: LogService,
  private val userStorageService: UserStorageService,
  private val configurationService: ConfigurationService
) : TrackViewModel(logService) {
  val user = userStorageService.user

  fun onSettingsClick(openScreen: (String) -> Unit) = openScreen(SETTINGS_SCREEN)

  fun onNameChange(newName: String) {
    launchCatching {
      userStorageService.updateUserName(newName.trim())
    }
  }

  fun onSpecialtyChange(newSpecialty: String) {
    launchCatching {
      userStorageService.updateUserSpecialty(newSpecialty)
    }
  }
}

package it.polimi.dima.track.model.service

interface ConfigurationService {
  suspend fun fetchConfiguration(): Boolean
  val isShowTaskEditButtonConfig: Boolean
}

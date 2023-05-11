package it.polimi.dima.track.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polimi.dima.track.common.snackbar.SnackBarManager
import it.polimi.dima.track.common.snackbar.SnackBarMessage.Companion.toSnackBarMessage
import it.polimi.dima.track.model.service.LogService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class TrackViewModel(private val logService: LogService) : ViewModel() {
  fun launchCatching(snackbar: Boolean = true, block: suspend CoroutineScope.() -> Unit) =
    viewModelScope.launch(
      CoroutineExceptionHandler { _, throwable ->
        if (snackbar) {
          SnackBarManager.showMessage(throwable.toSnackBarMessage())
        }
        logService.logNonFatalCrash(throwable)
      },
      block = block
    )
}

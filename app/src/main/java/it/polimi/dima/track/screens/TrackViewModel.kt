/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

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

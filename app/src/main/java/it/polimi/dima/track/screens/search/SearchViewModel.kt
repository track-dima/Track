package it.polimi.dima.track.screens.search

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.StorageService
import it.polimi.dima.track.screens.TrackViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
  logService: LogService,
  private val storageService: StorageService,
) : TrackViewModel(logService) {

  val searchText: MutableStateFlow<String> = MutableStateFlow("")

  @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
  val matchingTrainings: Flow<List<Training>> = searchText
    .debounce(300) // Debounce to prevent rapid updates while typing
    .flatMapLatest { query ->
      if (query.isEmpty()) {
        flowOf(emptyList()) // Emit an empty list if the query is empty
      } else {
        flow { emit(storageService.searchTrainings(query)) }
      }
    }
    .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  fun onSearchTextChanged(changedSearchText: String) {
    searchText.value = changedSearchText
  }

  fun onClearClick() {
    searchText.value = ""
  }
}

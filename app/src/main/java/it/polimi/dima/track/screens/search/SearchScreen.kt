package it.polimi.dima.track.screens.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.polimi.dima.track.R
import it.polimi.dima.track.common.utils.NavigationType
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.screens.agenda.AgendaTrainings

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SearchScreen(
  popUpScreen: () -> Unit,
  navigationType: NavigationType,
  viewModel: SearchViewModel = hiltViewModel(),
  onTrainingPressed: (Training) -> Unit
) {
  val searchText by viewModel.searchText.collectAsStateWithLifecycle("")
  val matchingTrainings by viewModel.matchingTrainings.collectAsStateWithLifecycle(emptyList())

  SearchBarUI(
    searchText = searchText,
    placeholderText = "Search trainings",
    onNavigateBack = popUpScreen,
    matchesFound = matchingTrainings.isNotEmpty(),
    onSearchTextChanged = { viewModel.onSearchTextChanged(it) },
    onClearClick = { viewModel.onClearClick() },
  ) {

    AgendaTrainings(
      trainings = matchingTrainings.sortedByDescending { it.dueDate },
      onTrainingPressed = onTrainingPressed,
      showActions = false
    )
  }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SearchBarUI(
  searchText: String,
  placeholderText: String = "",
  onSearchTextChanged: (String) -> Unit = {},
  onClearClick: () -> Unit = {},
  onNavigateBack: () -> Unit = {},
  matchesFound: Boolean,
  results: @Composable () -> Unit = {}
) {
  Box {
    Column(
      modifier = Modifier
        .fillMaxSize()
    ) {

      SearchBar(
        searchText,
        placeholderText,
        onSearchTextChanged,
        onClearClick,
        onNavigateBack
      )

      AnimatedContent(
        targetState = matchesFound,
        transitionSpec = {
          if (targetState) {
            slideInVertically { height -> height } + fadeIn() with fadeOut()
          } else {
            fadeIn() with fadeOut()
          }
      }, label = "Search results"
      ) {
        if (it) {
          results()
        } else {
          if (searchText.isNotEmpty()) {
            NoSearchResults(searchText)
          }
        }
      }
    }

  }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
  searchText: String,
  placeholderText: String = "",
  onSearchTextChanged: (String) -> Unit = {},
  onClearClick: () -> Unit = {},
  onNavigateBack: () -> Unit = {}
) {
  val keyboardController = LocalSoftwareKeyboardController.current
  val focusRequester = remember { FocusRequester() }

  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }


  TopAppBar(
    title = {
      OutlinedTextField(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 2.dp)
          .focusRequester(focusRequester),
        value = searchText,
        onValueChange = onSearchTextChanged,
        placeholder = {
          Text(text = placeholderText)
        },
        trailingIcon = {
          AnimatedVisibility(
            visible = searchText.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
          ) {
            IconButton(onClick = { onClearClick() }) {
              Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(id = R.string.close)
              )
            }

          }
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = Color.Transparent,
          unfocusedBorderColor = Color.Transparent,
        ),
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
          keyboardController?.hide()
        }),
      )
    },
    navigationIcon = {
      IconButton(onClick = { onNavigateBack() }) {
        Icon(
          imageVector = Icons.Rounded.ArrowBack,
          modifier = Modifier,
          contentDescription = stringResource(id = R.string.back)
        )
      }
    }
  )
}

@Composable
fun NoSearchResults(
  searchText: String = ""
) {
  Column(
    modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
    horizontalAlignment = CenterHorizontally
  ) {
    Text("No results for \"$searchText\"")
  }
}
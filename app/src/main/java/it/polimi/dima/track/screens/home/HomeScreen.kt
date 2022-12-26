package it.polimi.dima.track.screens.home

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.ext.toolbarActions

@Composable
fun HomeScreen(
    openScreen: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    ActionToolbar(
        title = R.string.home,
        modifier = Modifier.toolbarActions(),
        endActionIcon = R.drawable.ic_settings,
        endAction = { viewModel.onSettingsClick(openScreen) }
    )
}

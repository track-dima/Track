package it.polimi.dima.track.screens.agenda

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.ext.smallSpacer
import it.polimi.dima.track.common.ext.toolbarActions
import it.polimi.dima.track.common.utils.NavigationType
import it.polimi.dima.track.screens.trainings.TrainingItem


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AgendaScreen(
    openScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    navigationType: NavigationType,
    viewModel: AgendaViewModel = hiltViewModel()
) {
    Scaffold(
        floatingActionButton = {
            if (navigationType == NavigationType.BOTTOM_NAVIGATION) {
                FloatingActionButton(
                    onClick = { viewModel.onAddClick(openScreen) },
                    modifier = modifier.padding(16.dp)
                ) {
                    Icon(Icons.Filled.Add, "Add")
                }
            }
        }
    ) {
        val trainings = viewModel.trainings.collectAsStateWithLifecycle(emptyList())
        val options by viewModel.options

        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            ActionToolbar(
                title = R.string.agenda,
                modifier = Modifier.toolbarActions(),
                endActionIcon = R.drawable.ic_settings,
                endAction = { viewModel.onSettingsClick(openScreen) }
            )

            Spacer(modifier = Modifier.smallSpacer())

            LazyColumn {
                items(trainings.value, key = { it.id }) { trainingItem ->
                    TrainingItem(
                        training = trainingItem,
                        options = options,
                        onCheckChange = { viewModel.onTrainingCheckChange(trainingItem) },
                        onActionClick = { action -> viewModel.onTrainingActionClick(openScreen, trainingItem, action) }
                    )
                }
            }
        }
    }

    LaunchedEffect(viewModel) { viewModel.loadTaskOptions() }
}
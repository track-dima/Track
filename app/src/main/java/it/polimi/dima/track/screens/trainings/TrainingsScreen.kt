package it.polimi.dima.track.screens.trainings

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
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.ext.smallSpacer
import it.polimi.dima.track.common.ext.toolbarActions


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun TrainingsScreen(
    openScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrainingsViewModel = hiltViewModel()
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAddClick(openScreen) },
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = modifier.padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, "Add")
            }
        }
    ) {
        val trainings = viewModel.trainings.collectAsStateWithLifecycle(emptyList())
        val options by viewModel.options

        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            ActionToolbar(
                title = R.string.trainings,
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

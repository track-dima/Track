package it.polimi.dima.track.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.composable.DialogCancelButton
import it.polimi.dima.track.common.composable.DialogConfirmButton
import it.polimi.dima.track.common.ext.bigSpacer
import it.polimi.dima.track.common.ext.smallSpacer
import it.polimi.dima.track.common.ext.spacer
import it.polimi.dima.track.common.ext.toolbarActions
import it.polimi.dima.track.model.User


@Composable
fun ProfileScreen(
  openScreen: (String) -> Unit,
  viewModel: ProfileViewModel = hiltViewModel(),
) {

  Column(
    modifier = Modifier.fillMaxSize()
  ) {
    ActionToolbar(
      title = R.string.profile,
      modifier = Modifier.toolbarActions(),
      endActionIcon = Icons.Default.Settings,
      endActionDescription = R.string.settings,
      endAction = { viewModel.onSettingsClick(openScreen) }
    ) { }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
      val user = viewModel.user.collectAsStateWithLifecycle(User())

      UserInformation(
        user = user.value,
        onEditName = { viewModel.onNameChange(it) },
        onEditSpecialty = { viewModel.onSpecialtyChange(it) }
      )
    }
  }
}

@Composable
fun OutlinedCardWithHeader(
  header: String,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  iconContentDescription: String = "",
  content: @Composable () -> Unit
) {
  OutlinedCard(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      OutlinedCardHeader(icon, iconContentDescription, header)
      Divider(modifier = Modifier.padding(vertical = 16.dp))
      content()
    }
  }
}

@Composable
private fun OutlinedCardHeader(
  icon: ImageVector?,
  iconContentDescription: String,
  header: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    if (icon != null) {
      Icon(
        imageVector = icon,
        contentDescription = iconContentDescription,
        modifier = Modifier.padding(end = 16.dp)
      )
    }
    Text(
      text = header,
      style = MaterialTheme.typography.bodyLarge
    )
  }
}

@Composable
fun UserInformation(
  user: User,
  onEditName: (newName: String) -> Unit = { },
  onEditSpecialty: (newSpecialty: String) -> Unit = { },
) {
  OutlinedCardWithHeader(
    header = stringResource(id = R.string.user_information),
    icon = Icons.Rounded.AccountCircle,
    iconContentDescription = stringResource(id = R.string.user_information)
  ) {
    UserName(
      user = user,
      onEditName = onEditName
    )
    Spacer(modifier = Modifier.spacer())
    UserSpecialty(
      user = user,
      onEditSpecialty = onEditSpecialty
    )
  }
}

@Composable
private fun UserName(
  user: User,
  onEditName: (newName: String) -> Unit = { },
) {
  var openNameDialog by rememberSaveable { mutableStateOf(false) }

  if (openNameDialog) {
    SetNameDialog(
      title = stringResource(R.string.set_your_name),
      name = user.name,
      onConfirmClick = { newValue ->
        openNameDialog = false
        onEditName(newValue)
      },
      onDismissRequest = { openNameDialog = false }
    )
  }

  ModifiableField(
    text = user.name,
    placeholder = stringResource(R.string.set_your_name),
    onEditClick = { openNameDialog = true },
    editLabel = stringResource(R.string.edit_name),
    icon = Icons.Rounded.Badge,
    iconContentDescription = stringResource(R.string.user_name)
  )
}

@Composable
private fun UserSpecialty(
  user: User,
  onEditSpecialty: (newSpecialty: String) -> Unit = { },
) {
  var openSpecialtyDialog by rememberSaveable { mutableStateOf(false) }

  if (openSpecialtyDialog) {
    SetSpecialtyDialog(
      title = stringResource(R.string.set_your_specialty),
      specialty = user.specialty,
      onConfirmClick = { newValue ->
        openSpecialtyDialog = false
        onEditSpecialty(newValue)
      },
      onDismissRequest = { openSpecialtyDialog = false }
    )
  }

  ModifiableField(
    text = user.specialty,
    placeholder = stringResource(R.string.set_your_specialty),
    onEditClick = { openSpecialtyDialog = true },
    editLabel = stringResource(R.string.edit_specialty),
    icon = Icons.Rounded.DirectionsRun,
    iconContentDescription = stringResource(R.string.user_specialty)
  )
}

@Composable
private fun SetNameDialog(
  title: String,
  name: String,
  onConfirmClick: (String) -> Unit,
  onDismissRequest: () -> Unit
) {
  var text by rememberSaveable { mutableStateOf(name) }

  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = { Text(text = title) },
    text = {
      OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text(text = "Complete name") },
        singleLine = true
      )
    },
    confirmButton = {
      DialogConfirmButton(text = R.string.confirm) {
        onConfirmClick(text)
      }
    },
    dismissButton = {
      DialogCancelButton(text = R.string.cancel) {
        onDismissRequest()
      }
    }
  )
}

@Composable
private fun SetSpecialtyDialog(
  title: String,
  specialty: String,
  onConfirmClick: (String) -> Unit,
  onDismissRequest: () -> Unit
) {
  val radioOptions = listOf(
    "Sprint",
    "Middle-distance",
    "Long-distance",
    "Hurdles",
    "Jumps",
    "Throws",
    "Combined events",
    "Racewalking",
    "Road running",
    "Trail running",
    "Marathon"
  )
  val (selectedOption, onOptionSelected) = rememberSaveable { mutableStateOf(specialty.ifBlank { radioOptions[0] }) }

  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = { Text(text = title) },
    text = {
      Divider()
      Column(
        Modifier
          .selectableGroup()
          .height(280.dp)
          .verticalScroll(rememberScrollState())
      ) {
        radioOptions.forEach { text ->
          Row(
            Modifier
              .fillMaxWidth()
              .height(56.dp)
              .selectable(
                selected = (text == selectedOption),
                onClick = { onOptionSelected(text) },
                role = Role.RadioButton
              )
              .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            RadioButton(
              selected = (text == selectedOption),
              onClick = null // null recommended for accessibility with screenreaders
            )
            Text(
              text = text,
              style = MaterialTheme.typography.bodyLarge,
              modifier = Modifier.padding(start = 16.dp)
            )
          }
        }
      }
    },
    confirmButton = {
      DialogConfirmButton(text = R.string.confirm) {
        onConfirmClick(selectedOption)
      }
    },
    dismissButton = {
      DialogCancelButton(text = R.string.cancel) {
        onDismissRequest()
      }
    }
  )
}

@Composable
private fun ModifiableField(
  text: String,
  placeholder: String,
  onEditClick: () -> Unit,
  editLabel: String,
  icon: ImageVector,
  iconContentDescription: String = "",
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      icon,
      contentDescription = iconContentDescription,
      modifier = Modifier.padding(end = 16.dp)
    )
    if (text.isNotBlank()) Text(text = text)
    else Text(
      text = placeholder,
      color = LocalContentColor.current.copy(alpha = 0.5f)
    )
    Spacer(modifier = Modifier.weight(1f))
    FilledTonalIconButton(onClick = onEditClick) {
      Icon(
        Icons.Rounded.Edit,
        contentDescription = editLabel
      )
    }
  }
}

package it.polimi.dima.track.ui.training

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import it.polimi.dima.track.HiltTestActivity
import it.polimi.dima.track.R
import it.polimi.dima.track.data.emptyTraining
import it.polimi.dima.track.data.mockedTrainings
import it.polimi.dima.track.injection.InjectingTestCase
import it.polimi.dima.track.screens.training.TrainingScreen
import it.polimi.dima.track.utlis.onNodeWithContentDescriptionForStringId
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class TrainingMissingInformationTest : InjectingTestCase() {

  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @Test
  fun trainingScreen_noTitle() {
    composeTestRule.setContent {
      TrainingScreen(
        openScreen = {_ -> },
        trainingId = emptyTraining.id,
        onEditPressed = { _, _ -> },
      )
    }

    // The title icon (and text) is not shown
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.title
    ).assertDoesNotExist()
  }

  @Test
  fun trainingScreen_noDescription() {
    composeTestRule.setContent {
      TrainingScreen(
        openScreen = {_ -> },
        trainingId = emptyTraining.id,
        onEditPressed = { _, _ -> },
      )
    }

    // The description icon (and text) is not shown
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.description
    ).assertDoesNotExist()
  }

  @Test
  fun trainingScreen_noNotes() {
    composeTestRule.setContent {
      TrainingScreen(
        openScreen = {_ -> },
        trainingId = emptyTraining.id,
        onEditPressed = { _, _ -> },
      )
    }

    // The notes icon (and text) is not shown
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.notes
    ).assertDoesNotExist()
  }

  @Test
  fun trainingScreen_noType() {
    composeTestRule.setContent {
      TrainingScreen(
        openScreen = {_ -> },
        trainingId = emptyTraining.id,
        onEditPressed = { _, _ -> },
      )
    }

    // The type icon (and text) is not shown
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.type
    ).assertDoesNotExist()
  }

  @Test
  fun trainingScreen_noTime() {
    composeTestRule.setContent {
      TrainingScreen(
        openScreen = {_ -> },
        trainingId = emptyTraining.id,
        onEditPressed = { _, _ -> },
      )
    }

    // The time icon (and text) is not shown
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.date
    ).assertDoesNotExist()
  }

  @Test
  fun trainingScreen_allInformation() {
    composeTestRule.setContent {
      TrainingScreen(
        openScreen = {_ -> },
        trainingId = mockedTrainings[0].id,
        onEditPressed = { _, _ -> },
      )
    }

    // The title icon (and text) is shown
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.title
    ).assertIsDisplayed()

    // The description icon (and text) is shown
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.description
    ).assertIsDisplayed()

    // The notes icon (and text) is shown
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.notes
    ).assertIsDisplayed()

    // The type icon (and text) is shown
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.type
    ).assertIsDisplayed()

    // The time icon (and text) is shown
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.date
    ).assertIsDisplayed()
  }
}
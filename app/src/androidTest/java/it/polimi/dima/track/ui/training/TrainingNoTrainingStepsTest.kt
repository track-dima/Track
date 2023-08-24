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
import it.polimi.dima.track.utlis.onNodeWithStringId
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class TrainingNoTrainingStepsTest : InjectingTestCase() {

  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @Test
  fun trainingScreen_noTrainingSteps() {
    composeTestRule.setContent {
      TrainingScreen(
        openScreen = {_ -> },
        trainingId = emptyTraining.id,
        onEditPressed = {},
      )
    }

    // The no training steps string is shown
    composeTestRule.onNodeWithStringId(
      R.string.no_training_steps
    ).assertIsDisplayed()
  }

  @Test
  fun trainingScreen_withTrainingSteps() {
    composeTestRule.setContent {
      TrainingScreen(
        openScreen = {_ -> },
        trainingId = mockedTrainings[0].id,
        onEditPressed = {},
      )
    }

    // The no training steps string is not shown
    composeTestRule.onNodeWithStringId(
      R.string.no_training_steps
    ).assertDoesNotExist()

    // The fill training FAB is shown
    composeTestRule.onNodeWithStringId(
      R.string.fill_training, useUnmergedTree = true
    ).assertExists()
  }
}
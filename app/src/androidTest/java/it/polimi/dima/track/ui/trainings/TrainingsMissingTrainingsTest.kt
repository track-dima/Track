package it.polimi.dima.track.ui.trainings

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import it.polimi.dima.track.HiltTestActivity
import it.polimi.dima.track.R
import it.polimi.dima.track.TrackApp
import it.polimi.dima.track.data.mockedTrainings
import it.polimi.dima.track.injection.InjectingTestCase
import it.polimi.dima.track.utils.compactWindowSize
import it.polimi.dima.track.utils.onNodeWithContentDescriptionForStringId
import it.polimi.dima.track.utils.onNodeWithStringId
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class TrainingsMissingTrainingsTest : InjectingTestCase() {

  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalTestApi::class)
  @Test
  fun trainingsScreen_missingNextTraining() {
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = compactWindowSize),
        width = compactWindowSize.width
      )
    }

    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.trainings
    ).assertIsDisplayed()

    // delete next training
    composeTestRule.waitUntilExactlyOneExists(
      matcher = hasText(mockedTrainings[0].title),
      timeoutMillis = 5000
    )
    composeTestRule.onNodeWithText(
      mockedTrainings[0].title
    ).performClick()
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.more_options
    ).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithStringId(
      R.string.delete_training
    ).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithStringId(
      R.string.delete
    ).assertIsDisplayed().performClick()

    // verify that the next training is not shown
    composeTestRule.onNodeWithStringId(
      R.string.no_planned_trainings
    ).assertIsDisplayed()

    // while the last training is still shown
    composeTestRule.onNodeWithText(
      mockedTrainings[1].title
    ).assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalTestApi::class)
  @Test
  fun trainingsScreen_missingLastTraining() {
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = compactWindowSize),
        width = compactWindowSize.width
      )
    }

    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.trainings
    ).assertIsDisplayed()

    // delete all last trainings
    for (i in 1..mockedTrainings.lastIndex) {
      composeTestRule.waitUntilExactlyOneExists(
        matcher = hasText(mockedTrainings[i].title),
        timeoutMillis = 5000
      )
      composeTestRule.onNodeWithText(
        mockedTrainings[i].title
      ).performClick()
      composeTestRule.onNodeWithContentDescriptionForStringId(
        R.string.more_options
      ).assertIsDisplayed().performClick()
      composeTestRule.onNodeWithStringId(
        R.string.delete_training
      ).assertIsDisplayed().performClick()
      composeTestRule.onNodeWithStringId(
        R.string.delete
      ).assertIsDisplayed().performClick()
    }

    // verify that the last training is not shown
    composeTestRule.onNodeWithStringId(
      R.string.no_trainings_in_history
    ).assertIsDisplayed()

    // while the next training is still shown
    composeTestRule.onNodeWithText(
      mockedTrainings[0].title
    ).assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalTestApi::class)
  @Test
  fun trainingScreen_noTrainings() {
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = compactWindowSize),
        width = compactWindowSize.width
      )
    }

    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.trainings
    ).assertIsDisplayed()

    // delete all trainings
    for (i in mockedTrainings.indices) {
      composeTestRule.waitUntilExactlyOneExists(
        matcher = hasText(mockedTrainings[i].title),
        timeoutMillis = 5000
      )
      composeTestRule.onNodeWithText(
        mockedTrainings[i].title
      ).performClick()
      composeTestRule.onNodeWithContentDescriptionForStringId(
        R.string.more_options
      ).assertIsDisplayed().performClick()
      composeTestRule.onNodeWithStringId(
        R.string.delete_training
      ).assertIsDisplayed().performClick()
      composeTestRule.onNodeWithStringId(
        R.string.delete
      ).assertIsDisplayed().performClick()
    }

    // There is no next training
    composeTestRule.onNodeWithStringId(
      R.string.no_planned_trainings
    ).assertIsDisplayed()

    // There is no last training
    composeTestRule.onNodeWithStringId(
      R.string.no_trainings_in_history
    ).assertIsDisplayed()
  }
}
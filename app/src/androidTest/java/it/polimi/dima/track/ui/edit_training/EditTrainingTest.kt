package it.polimi.dima.track.ui.edit_training

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
class EditTrainingTest : InjectingTestCase() {

  /**
   * Note: To access to an empty activity, the code uses a custom activity [HiltTestActivity]
   * annotated with @AndroidEntryPoint.
   */
  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalTestApi::class)
  @Test
  fun editTrainingScreen_modifyTraining_appBarEditTitle() {
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = compactWindowSize),
        width = compactWindowSize.width
      )
    }

    // Open first training
    composeTestRule.waitUntilExactlyOneExists(
      matcher = hasText(mockedTrainings[0].title),
      timeoutMillis = 5000
    )
    composeTestRule.onNodeWithText(
      mockedTrainings[0].title
    ).assertIsDisplayed().performClick()

    // Edit training
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.edit_training
    ).assertIsDisplayed().performClick()

    // Check app bar title
    composeTestRule.onNodeWithStringId(
      R.string.edit_training
    ).assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalTestApi::class)
  @Test
  fun editTrainingScreen_duplicateTraining_appBarDuplicateTitle() {
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = compactWindowSize),
        width = compactWindowSize.width
      )
    }

    // Open first training
    composeTestRule.waitUntilExactlyOneExists(
      matcher = hasText(mockedTrainings[0].title),
      timeoutMillis = 5000
    )
    composeTestRule.onNodeWithText(
      mockedTrainings[0].title
    ).assertIsDisplayed().performClick()

    // Edit training
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.more_options
    ).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithStringId(
      R.string.duplicate_training
    ).assertIsDisplayed().performClick()

    // Check app bar title
    composeTestRule.onNodeWithStringId(
      R.string.duplicate_training
    ).assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalTestApi::class)
  @Test
  fun editTrainingScreen_addTraining_appBarAddTitle() {
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = compactWindowSize),
        width = compactWindowSize.width
      )
    }

    // Add training
    composeTestRule.waitUntilExactlyOneExists(
      matcher = hasText(mockedTrainings[0].title),
      timeoutMillis = 5000
    )
    composeTestRule.onNodeWithStringId(
      R.string.add_training,
      useUnmergedTree = true
    ).assertIsDisplayed().performClick()

    // Check app bar title
    composeTestRule.onNodeWithStringId(
      R.string.new_training
    ).assertIsDisplayed()
  }
}
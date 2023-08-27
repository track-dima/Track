package it.polimi.dima.track.ui.edit_training

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import it.polimi.dima.track.HiltTestActivity
import it.polimi.dima.track.R
import it.polimi.dima.track.TestCompactWidth
import it.polimi.dima.track.TestExpandedTabletWidth
import it.polimi.dima.track.TrackApp
import it.polimi.dima.track.data.mockedTrainings
import it.polimi.dima.track.injection.InjectingTestCase
import it.polimi.dima.track.utlis.compactWindowSize
import it.polimi.dima.track.utlis.expandedTabletWindowSize
import it.polimi.dima.track.utlis.onNodeWithContentDescriptionForStringId
import it.polimi.dima.track.utlis.onNodeWithStringId
import it.polimi.dima.track.utlis.onNodeWithTagForStringId
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class EditTrainingRestorationTest : InjectingTestCase() {

  /**
   * Note: To access to an empty activity, the code uses a custom activity [HiltTestActivity]
   * annotated with @AndroidEntryPoint.
   */
  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalTestApi::class)
  @Test
  fun editTrainingScreen_modifyTrainingTrainingUnchanged_afterConfigChange() {
    val stateRestorationTester = StateRestorationTester(composeTestRule)
    stateRestorationTester.setContent {
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

    // Set new title
    composeTestRule.onNodeWithTagForStringId(
      R.string.title_input_tag
    ).performTextReplacement("New title")
    composeTestRule.onNodeWithTagForStringId(
      R.string.title_input_tag
    ).assertIsDisplayed().assertTextEquals("New title")

    // Simulate a config change
    stateRestorationTester.emulateSavedInstanceStateRestore()

    // Verify that the title is still the new one
    composeTestRule.onNodeWithTagForStringId(
      R.string.title_input_tag
    ).assertIsDisplayed().assertTextEquals("New title")
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalTestApi::class)
  @Test
  fun editTrainingScreen_modifyTrainingEditStepsTrainingUnchanged_afterConfigChange() {
    val stateRestorationTester = StateRestorationTester(composeTestRule)
    stateRestorationTester.setContent {
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

    // Set new title
    composeTestRule.onNodeWithTagForStringId(
      R.string.title_input_tag
    ).performTextReplacement("New title")
    composeTestRule.onNodeWithTagForStringId(
      R.string.title_input_tag
    ).assertIsDisplayed().assertTextEquals("New title")

    // Open edit steps
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.edit_repetitions,
      useUnmergedTree = true
    ).assertIsDisplayed().performClick()

    // Simulate a config change
    stateRestorationTester.emulateSavedInstanceStateRestore()

    // Close edit steps
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.close,
    ).assertIsDisplayed().performClick()

    // Verify that the title is still the new one
    composeTestRule.onNodeWithTagForStringId(
      R.string.title_input_tag
    ).assertIsDisplayed().assertTextEquals("New title")
  }
}
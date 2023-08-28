package it.polimi.dima.track.ui.trainings

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class TrainingsRestorationTest : InjectingTestCase() {

  /**
   * Note: To access to an empty activity, the code uses a custom activity [HiltTestActivity]
   * annotated with @AndroidEntryPoint.
   */
  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestCompactWidth
  fun compactDevice_trainingsScreenSelectedTrainingTrainingRetained_afterConfigChange() {
    // Setup compact window
    val stateRestorationTester = StateRestorationTester(composeTestRule)
    stateRestorationTester.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = compactWindowSize),
        width = compactWindowSize.width
      )
    }

    // The two trainings that are shown in trainings screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.trainings
    ).assertIsDisplayed()
    composeTestRule.onNodeWithText(
      mockedTrainings[0].title
    ).assertExists()
    composeTestRule.onNodeWithText(
      mockedTrainings[1].title
    ).assertExists()

    // Open first training
    composeTestRule.onNodeWithText(
      mockedTrainings[0].title
    ).assertIsDisplayed().performClick()

    // Verify that it shows the detailed screen for the correct training
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.close
    ).assertExists()
    composeTestRule.onNodeWithText(
      mockedTrainings[0].notes
    ).assertExists()

    // Simulate a config change
    stateRestorationTester.emulateSavedInstanceStateRestore()

    // Verify that it still shows the detailed screen for the same training
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.close
    ).assertExists()
    composeTestRule.onNodeWithText(
      mockedTrainings[0].notes
    ).assertExists()
  }


  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestExpandedTabletWidth
  fun expandedTabletDevice_trainingsScreenSelectedTrainingTrainingRetained_afterConfigChange() {
    // Setup compact window
    val stateRestorationTester = StateRestorationTester(composeTestRule)
    stateRestorationTester.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = expandedTabletWindowSize),
        width = expandedTabletWindowSize.width
      )
    }

    // Select first training
    composeTestRule.onNodeWithText(
      mockedTrainings[0].title
    ).assertIsDisplayed().performClick()

    // Verify that first training is displayed on the details screen
    composeTestRule.onNodeWithText(
      mockedTrainings[0].notes
    ).assertExists()
    composeTestRule.onNodeWithText(
      mockedTrainings[1].notes
    ).assertDoesNotExist()

    // Simulate a config change
    stateRestorationTester.emulateSavedInstanceStateRestore()

    // Verify that first training is still displayed on the details screen
    composeTestRule.onNodeWithText(
      mockedTrainings[0].notes
    ).assertExists()
    composeTestRule.onNodeWithText(
      mockedTrainings[1].notes
    ).assertDoesNotExist()
  }
}
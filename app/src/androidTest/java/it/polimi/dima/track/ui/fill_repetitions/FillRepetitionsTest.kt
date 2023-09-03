package it.polimi.dima.track.ui.fill_repetitions

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
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
import it.polimi.dima.track.utils.onNodeWithStringId
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class FillRepetitionsTest : InjectingTestCase() {

  /**
   * Note: To access to an empty activity, the code uses a custom activity [HiltTestActivity]
   * annotated with @AndroidEntryPoint.
   */
  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @Test
  fun fillRepetitions_StepExistsAndIsClickable() {
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = compactWindowSize),
        width = compactWindowSize.width
      )
    }

    composeTestRule.waitUntilExactlyOneExists(
      hasText(mockedTrainings[0].title),
      timeoutMillis = 5_000L
    )

    composeTestRule.onNodeWithText(mockedTrainings[0].title)
      .assertExists()
      .assertIsDisplayed()
      .assertHasClickAction()
      .performClick()

    composeTestRule.onNodeWithText(
      composeTestRule.activity.getString(R.string.fill_training),
      useUnmergedTree = true
    )
      .assertExists()
      .assertIsDisplayed()
      .performClick()

    composeTestRule.waitUntilAtLeastOneExists(
      hasText(composeTestRule.activity.getString(R.string.warm_up))
    )

    composeTestRule.onNodeWithStringId(R.string.warm_up)
      .assertExists()
      .assertIsDisplayed()
      .assertHasClickAction()
  }
}
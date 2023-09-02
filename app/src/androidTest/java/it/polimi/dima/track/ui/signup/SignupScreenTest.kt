package it.polimi.dima.track.ui.signup

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import it.polimi.dima.track.HiltTestActivity
import it.polimi.dima.track.R
import it.polimi.dima.track.TrackApp
import it.polimi.dima.track.injection.InjectingTestCase
import it.polimi.dima.track.utils.compactWindowSize
import it.polimi.dima.track.utils.onNodeWithContentDescriptionForStringId
import it.polimi.dima.track.utils.onNodeWithStringId
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SignupScreenTest : InjectingTestCase() {

  /**
   * Note: To access to an empty activity, the code uses a custom activity [HiltTestActivity]
   * annotated with @AndroidEntryPoint.
   */
  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @Test
  fun signupScreen() {
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = compactWindowSize),
        width = compactWindowSize.width
      )
    }

    composeTestRule.waitUntilAtLeastOneExists(
      matcher = hasContentDescription(composeTestRule.activity.getString(R.string.settings)),
      timeoutMillis = 5_000L
    )

    composeTestRule.onNodeWithContentDescriptionForStringId(R.string.settings)
      .assertExists()
      .performClick()

    composeTestRule.onNodeWithStringId(R.string.create_account)
      .assertExists()
      .performClick()

    composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.email))
      .assertExists()
      .assertIsDisplayed()
      .assertIsEnabled()
      .performClick()
      .assertIsFocused()

    composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.password))
      .assertExists()
      .assertIsDisplayed()
      .assertIsEnabled()
      .performClick()
      .assertIsFocused()

    composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.repeat_password))
      .assertExists()
      .assertIsDisplayed()
      .assertIsEnabled()
      .performClick()
      .assertIsFocused()

    composeTestRule.onNode(hasText("Create account") and hasClickAction())
      .assertExists()
      .assertIsDisplayed()
      .assertIsEnabled()
  }
}
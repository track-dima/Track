package it.polimi.dima.track.ui.login

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
class LoginScreenTest : InjectingTestCase() {

  /**
   * Note: To access to an empty activity, the code uses a custom activity [HiltTestActivity]
   * annotated with @AndroidEntryPoint.
   */
  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @Test
  fun loginScreen_AccessibleAndComposed() {
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

    composeTestRule.onNodeWithStringId(R.string.sign_in)
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

    composeTestRule.onNodeWithText("Login")
      .assertExists()
      .assertIsDisplayed()
      .assertIsEnabled()
  }

  @Test
  fun loginScreen_LoginAndLogoutFlow() {
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

    composeTestRule.onNodeWithStringId(R.string.sign_in)
      .assertExists()
      .performClick()

    composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.email))
      .assertExists()
      .assertIsDisplayed()
      .assertIsEnabled()
      .performClick()
      .assertIsFocused()
      .performTextInput("luccioletti.fabio@gmail.com")

    composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.password))
      .assertExists()
      .assertIsDisplayed()
      .assertIsEnabled()
      .performClick()
      .assertIsFocused()
      .performTextInput("P4ssw0rd")

    composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.sign_in))
      .assertExists()
      .assertIsDisplayed()
      .assertIsEnabled()
      .assertHasClickAction()
      .performClick()

    composeTestRule.waitUntilExactlyOneExists(
      hasContentDescription(composeTestRule.activity.getString(R.string.settings)),
      timeoutMillis = 5_000L,
    )

    composeTestRule.onNodeWithContentDescriptionForStringId(R.string.settings)
      .assertExists()
      .assertIsDisplayed()
      .assertHasClickAction()
      .performClick()

    composeTestRule.waitUntilExactlyOneExists(
      hasText(composeTestRule.activity.getString(R.string.sign_out)),
      timeoutMillis = 5_000L,
    )

    composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.sign_out))
      .assertExists()
      .assertIsDisplayed()
      .assertHasClickAction()
      .performClick()
  }
}
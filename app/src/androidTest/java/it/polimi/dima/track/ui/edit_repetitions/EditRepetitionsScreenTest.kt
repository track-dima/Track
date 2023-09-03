package it.polimi.dima.track.ui.edit_repetitions

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import it.polimi.dima.track.HiltTestActivity
import it.polimi.dima.track.injection.InjectingTestCase
import it.polimi.dima.track.screens.edit_repetitions.EditRepetitionsScreen
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class EditRepetitionsScreenTest : InjectingTestCase() {
  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @Test
  fun editRepetitionsScreen_Repetitions() {
    composeTestRule.setContent {
      EditRepetitionsScreen(popUpScreen = { })
    }

    composeTestRule
      .onNodeWithText("3x", useUnmergedTree = true)
      .assertExists()
      .assertIsDisplayed()
      .performClick()

    composeTestRule
      .onNodeWithText("Add repetition")
      .assertExists()
      .assertIsDisplayed()
      .performClick()

    composeTestRule
      .onNodeWithText("Save")
      .assertExists()
      .assertIsDisplayed()
      .performClick()
  }
}
package it.polimi.dima.track.ui.edit_repetitions

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
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
  fun editRepetitionsScreen() {
    composeTestRule.setContent {
      EditRepetitionsScreen(popUpScreen = { })
    }

    composeTestRule
      .onNodeWithText("Add repetition")
      .assertExists()
      .assertIsDisplayed()
  }
}
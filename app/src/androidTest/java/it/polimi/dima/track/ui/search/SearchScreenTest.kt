package it.polimi.dima.track.ui.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidTest
import it.polimi.dima.track.HiltTestActivity
import it.polimi.dima.track.injection.InjectingTestCase
import it.polimi.dima.track.screens.search.SearchScreen
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SearchScreenTest : InjectingTestCase() {
  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @Test
  fun searchScreen() {
    composeTestRule.setContent {
      SearchScreen(popUpScreen = {}, onTrainingPressed = {})
    }

    composeTestRule
      .onNodeWithText("Search trainings")
      .assertExists()
      .assertIsDisplayed()
      .assertIsFocused()
      .performTextInput("Query")
  }
}
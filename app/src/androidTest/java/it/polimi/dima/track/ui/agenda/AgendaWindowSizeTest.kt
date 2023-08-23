package it.polimi.dima.track.ui.agenda

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import it.polimi.dima.track.HiltTestActivity
import it.polimi.dima.track.R
import it.polimi.dima.track.TestCompactWidth
import it.polimi.dima.track.TestExpandedTabletWidth
import it.polimi.dima.track.TestExpandedWidth
import it.polimi.dima.track.TestMediumWidth
import it.polimi.dima.track.TrackApp
import it.polimi.dima.track.data.mockedTrainings
import it.polimi.dima.track.injection.InjectingTestCase
import it.polimi.dima.track.utlis.compactWindowSize
import it.polimi.dima.track.utlis.expandedTabletWindowSize
import it.polimi.dima.track.utlis.expandedWindowSize
import it.polimi.dima.track.utlis.mediumWindowSize
import it.polimi.dima.track.utlis.onNodeWithContentDescriptionForStringId
import it.polimi.dima.track.utlis.onNodeWithTagForStringId
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AgendaWindowSizeTest : InjectingTestCase() {

  /**
   * Note: To access to an empty activity, the code uses a custom activity [HiltTestActivity]
   * annotated with @AndroidEntryPoint.
   */
  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestCompactWidth
  fun compactDevice_agendaScreen() {
    // Setup compact window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = compactWindowSize),
        width = compactWindowSize.width
      )
    }

    // Open agenda screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.agenda
    ).assertIsDisplayed().performClick()

    // Training card is shown
    composeTestRule.onNodeWithText(
      mockedTrainings[0].title
    ).assertExists()

    // Training is not shown in compact mode
    composeTestRule.onNodeWithText(
      mockedTrainings[0].notes
    ).assertDoesNotExist()

    // The add Fab is shown
    composeTestRule.onNodeWithTagForStringId(
      R.string.add_training_agenda_fab_tag
    ).assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestMediumWidth
  fun mediumDevice_agendaScreen() {
    // Setup medium window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = mediumWindowSize),
        width = mediumWindowSize.width
      )
    }

    // Open agenda screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.agenda
    ).assertIsDisplayed().performClick()

    // Training card is shown
    composeTestRule.onNodeWithText(
      mockedTrainings[0].title
    ).assertExists()

    // Training is not shown in medium mode
    composeTestRule.onNodeWithText(
      mockedTrainings[0].notes
    ).assertDoesNotExist()

    // The add Fab is not shown
    composeTestRule.onNodeWithTagForStringId(
      R.string.add_training_agenda_fab_tag
    ).assertDoesNotExist()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestExpandedWidth
  fun expandedDevice_agendaScreen() {
    // Setup expanded window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = expandedWindowSize),
        width = expandedWindowSize.width
      )
    }

    // Open agenda screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.agenda
    ).assertIsDisplayed().performClick()

    // Training card is shown
    composeTestRule.onNodeWithText(
      mockedTrainings[0].title
    ).assertExists()

    // Training is not shown in expanded mode (not tablet)
    composeTestRule.onNodeWithText(
      mockedTrainings[0].notes
    ).assertDoesNotExist()

    // The add Fab is not shown
    composeTestRule.onNodeWithTagForStringId(
      R.string.add_training_agenda_fab_tag
    ).assertDoesNotExist()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestExpandedTabletWidth
  fun expandedTabletDevice_agendaScreen() {
    // Setup expanded tablet window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = expandedTabletWindowSize),
        width = expandedTabletWindowSize.width
      )
    }

    // Open agenda screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.agenda
    ).assertIsDisplayed().performClick()

    // Training card is shown (and the training in the details screen)
    composeTestRule.onAllNodesWithText(
      mockedTrainings[0].title
    ).assertCountEquals(2)

    // Training is shown in tablet expanded mode
    composeTestRule.onNodeWithText(
      mockedTrainings[1].title
    ).assertExists()

    // In this case the close button is not shown
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.close
    ).assertDoesNotExist()

    // The add Fab is not shown
    composeTestRule.onNodeWithTagForStringId(
      R.string.add_training_agenda_fab_tag
    ).assertDoesNotExist()
  }
}
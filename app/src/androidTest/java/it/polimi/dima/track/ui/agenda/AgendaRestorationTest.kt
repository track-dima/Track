package it.polimi.dima.track.ui.agenda

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import it.polimi.dima.track.HiltTestActivity
import it.polimi.dima.track.R
import it.polimi.dima.track.TestCompactWidth
import it.polimi.dima.track.TestExpandedTabletWidth
import it.polimi.dima.track.TrackApp
import it.polimi.dima.track.data.mockedTrainings
import it.polimi.dima.track.injection.InjectingTestCase
import it.polimi.dima.track.injection.MockTrainingStorageServiceImpl
import it.polimi.dima.track.model.service.impl.storage.PersonalBestStorageServiceImpl
import it.polimi.dima.track.model.service.impl.storage.UserStorageServiceImpl
import it.polimi.dima.track.model.service.module.StorageModule
import it.polimi.dima.track.model.service.storage.PersonalBestStorageService
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import it.polimi.dima.track.model.service.storage.UserStorageService
import it.polimi.dima.track.utlis.compactWindowSize
import it.polimi.dima.track.utlis.expandedTabletWindowSize
import it.polimi.dima.track.utlis.onNodeWithContentDescriptionForStringId
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@UninstallModules(StorageModule::class)
@HiltAndroidTest
class AgendaRestorationTest : InjectingTestCase() {

  /**
   * Note: To access to an empty activity, the code uses a custom activity [HiltTestActivity]
   * annotated with @AndroidEntryPoint.
   */
  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @Module
  @InstallIn(SingletonComponent::class)
  abstract class TestServiceModule {
    @Binds
    abstract fun provideMockTrainingStorageService(impl: MockTrainingStorageServiceImpl): TrainingStorageService

    @Binds
    abstract fun provideMockPersonalBestStorageService(impl: PersonalBestStorageServiceImpl): PersonalBestStorageService

    @Binds
    abstract fun provideMockUserStorageService(impl: UserStorageServiceImpl): UserStorageService
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestCompactWidth
  fun compactDevice_agendaScreenSelectedTrainingTrainingRetained_afterConfigChange() {
    // Setup compact window
    val stateRestorationTester = StateRestorationTester(composeTestRule)
    stateRestorationTester.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = compactWindowSize),
        width = compactWindowSize.width
      )
    }

    // Open agenda screen (training is not shown is compact mode)
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.agenda
    ).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText(
      mockedTrainings[0].notes
    ).assertDoesNotExist()

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
  fun expandedTabletDevice_agendaScreenSelectedTrainingTrainingRetained_afterConfigChange() {
    // Setup tablet expanded window
    val stateRestorationTester = StateRestorationTester(composeTestRule)
    stateRestorationTester.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = expandedTabletWindowSize),
        width = expandedTabletWindowSize.width
      )
    }

    // Open agenda screen (training is shown is extended tablet mode)
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.agenda
    ).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText(
      mockedTrainings[0].notes
    ).assertExists()

    // Select second training
    composeTestRule.onNodeWithText(
      mockedTrainings[1].title
    ).assertIsDisplayed().performClick()

    // Verify that second training is displayed on the details screen
    composeTestRule.onNodeWithText(
      mockedTrainings[0].notes
    ).assertDoesNotExist()
    composeTestRule.onNodeWithText(
      mockedTrainings[1].notes
    ).assertExists()

    // Simulate a config change
    stateRestorationTester.emulateSavedInstanceStateRestore()

    // Verify that second training is still displayed on the details screen
    composeTestRule.onNodeWithText(
      mockedTrainings[0].notes
    ).assertDoesNotExist()
    composeTestRule.onNodeWithText(
      mockedTrainings[1].notes
    ).assertExists()
  }
}
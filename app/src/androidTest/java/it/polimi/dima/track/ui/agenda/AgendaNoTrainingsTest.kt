package it.polimi.dima.track.ui.agenda

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
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
import it.polimi.dima.track.TrackApp
import it.polimi.dima.track.data.mockedTrainings
import it.polimi.dima.track.injection.InjectingTestCase
import it.polimi.dima.track.injection.service.MockTrainingStorageServiceImpl
import it.polimi.dima.track.model.service.impl.storage.PersonalBestStorageServiceImpl
import it.polimi.dima.track.model.service.impl.storage.UserStorageServiceImpl
import it.polimi.dima.track.model.service.module.StorageModule
import it.polimi.dima.track.model.service.storage.PersonalBestStorageService
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import it.polimi.dima.track.model.service.storage.UserStorageService
import it.polimi.dima.track.utlis.compactWindowSize
import it.polimi.dima.track.utlis.onNodeWithContentDescriptionForStringId
import it.polimi.dima.track.utlis.onNodeWithStringId
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Singleton

@RunWith(AndroidJUnit4::class)
@UninstallModules(StorageModule::class)
@HiltAndroidTest
class AgendaNoTrainingsTest : InjectingTestCase() {

  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @Module
  @InstallIn(SingletonComponent::class)
  abstract class TestServiceModule {
    @Binds
    @Singleton
    abstract fun provideMockTrainingStorageService(impl: MockTrainingStorageServiceImpl): TrainingStorageService

    @Binds
    abstract fun provideMockPersonalBestStorageService(impl: PersonalBestStorageServiceImpl): PersonalBestStorageService

    @Binds
    abstract fun provideMockUserStorageService(impl: UserStorageServiceImpl): UserStorageService
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalTestApi::class)
  @Test
  fun agendaScreen_noTraining() {
    // TODO a volte fallisce se elimino direttamente da agenda
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = compactWindowSize),
        width = compactWindowSize.width
      )
    }

    // delete all trainings
    for (i in mockedTrainings.indices) {
      composeTestRule.waitUntilExactlyOneExists(
        matcher = hasText(mockedTrainings[i].title),
        timeoutMillis = 5000
      )
      composeTestRule.onNodeWithText(
        mockedTrainings[i].title
      ).performClick()
      composeTestRule.onNodeWithContentDescriptionForStringId(
        R.string.more_options
      ).assertIsDisplayed().performClick()
      composeTestRule.onNodeWithStringId(
        R.string.delete_training
      ).assertIsDisplayed().performClick()
      composeTestRule.onNodeWithStringId(
        R.string.delete
      ).assertIsDisplayed().performClick()
    }

    // Open agenda screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.agenda
    ).assertIsDisplayed().performClick()

    // The no training string is shown
    composeTestRule.onNodeWithStringId(
      R.string.no_trainings
    ).assertIsDisplayed()
  }
}
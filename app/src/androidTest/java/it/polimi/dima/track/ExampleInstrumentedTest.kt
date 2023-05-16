package it.polimi.dima.track

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.screens.edit_training.EditTrainingScreen
import it.polimi.dima.track.screens.edit_training.EditTrainingViewModel

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("it.polimi.dima.track", appContext.packageName)
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onRecreation_stateIsRestored() {
        val restorationTester = StateRestorationTester(composeTestRule)

        restorationTester.setContent {
            var isDialogShown by remember { mutableStateOf(false) }

            // EditTrainingScreen()
        }

        val dialog = composeTestRule.onNodeWithTag("datePickerDialog")
        dialog.assertExists()
        dialog.assertIsDisplayed()

        restorationTester.emulateSavedInstanceStateRestore()

        dialog.assertExists()
        dialog.assertIsDisplayed()
    }
}
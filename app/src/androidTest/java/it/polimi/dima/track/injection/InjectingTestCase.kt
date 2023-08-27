package it.polimi.dima.track.injection

import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule

abstract class InjectingTestCase {
  @get:Rule(order = 0)
  var hiltRule = HiltAndroidRule(this)

  @Before
  open fun setUp() {
    hiltRule.inject()
  }
}
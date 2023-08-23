package it.polimi.dima.track

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.core.view.WindowCompat
import androidx.window.layout.WindowMetricsCalculator
import dagger.hilt.android.AndroidEntryPoint
import it.polimi.dima.track.ui.theme.TrackTheme

@AndroidEntryPoint
class TrackActivity : AppCompatActivity() {
  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    WindowCompat.setDecorFitsSystemWindows(window, false)
    setContent {
      val windowSize = calculateWindowSizeClass(this)
      TrackTheme {
        TrackApp(
          windowSize = windowSize,
          width = calculateWidth(this)
        )
      }
    }
  }
}

@Composable
fun calculateWidth(activity: Activity): Dp {
  LocalConfiguration.current
  val density = LocalDensity.current
  val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(activity)
  return with(density) { metrics.bounds.toComposeRect().width.toDp() }
}
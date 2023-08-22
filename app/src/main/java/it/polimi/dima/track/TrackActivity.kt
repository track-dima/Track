package it.polimi.dima.track

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.WindowCompat
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
        )
      }
    }
  }
}
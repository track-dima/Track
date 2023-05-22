package it.polimi.dima.track

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import it.polimi.dima.track.screens.fitbit.FitbitScreen

class FitbitActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        setContent {
            FitbitScreen()
        }
    }
}

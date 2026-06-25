package com.skilltracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.skilltracker.ui.navigation.SkillTrackerNavGraph
import com.skilltracker.ui.theme.SkillTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as SkillTrackerApp

        setContent {
            SkillTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SkillTrackerNavGraph(repository = app.repository)
                }
            }
        }
    }
}

package com.ziyin.island

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ziyin.island.ui.ZiyinApp
import com.ziyin.island.ui.theme.ZiyinIslandTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZiyinIslandTheme {
                ZiyinApp()
            }
        }
    }
}


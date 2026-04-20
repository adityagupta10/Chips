package com.ean.chips

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ean.chips.ui.navigation.ChipsNavigation
import com.ean.chips.ui.theme.ChipsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChipsTheme {
                ChipsNavigation()
            }
        }
    }
}
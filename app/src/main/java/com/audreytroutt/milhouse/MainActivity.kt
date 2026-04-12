package com.audreytroutt.milhouse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.audreytroutt.milhouse.navigation.AppNavigation
import com.audreytroutt.milhouse.ui.theme.MilhouseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MilhouseTheme {
                AppNavigation()
            }
        }
    }
}

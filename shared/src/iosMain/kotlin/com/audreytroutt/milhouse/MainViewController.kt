package com.audreytroutt.milhouse

import androidx.compose.ui.window.ComposeUIViewController
import com.audreytroutt.milhouse.navigation.AppNavigation
import com.audreytroutt.milhouse.ui.theme.MilhouseTheme

fun MainViewController() = ComposeUIViewController {
    MilhouseTheme {
        AppNavigation()
    }
}

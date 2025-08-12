package com.fiserv.commercehub.androidttp


import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.fiserv.commercehub.androidttp.security.BackgroundWorkManager
import com.fiserv.commercehub.androidttp.ui.navigation.NavGraph
import com.fiserv.commercehub.androidttp.ui.theme.AndroidTapToPayDemoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BackgroundWorkManager.startPeriodicWorkManager(this)
        setContent {
            MainScreen()
        }
    }
}


@Composable
private fun MainScreen() {
    AndroidTapToPayDemoTheme {
        val navController = rememberNavController()
        NavGraph(navController)
    }
}

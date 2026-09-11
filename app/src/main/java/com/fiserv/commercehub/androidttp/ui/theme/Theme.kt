package com.fiserv.commercehub.androidttp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController



private val DarkColorPalette = darkColorScheme(
    primary = FiservColorCode,
    secondary = Teal200,
    tertiary = FiservColorCode
)

private val LightColorPalette = lightColorScheme(
    primary = FiservColorCode,
    secondary = Teal200,
    tertiary = FiservColorCode

)



@Composable
fun AndroidTapToPayDemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useSystemUiController: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    if (useSystemUiController) {
        val systemUiController = rememberSystemUiController()
        systemUiController.setStatusBarColor(
            color = colors.primary
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
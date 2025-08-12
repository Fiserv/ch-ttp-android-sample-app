package com.fiserv.commercehub.androidttp.ui.main.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiserv.commercehub.androidttp.BuildConfig
import com.fiserv.commercehub.androidttp.R
import com.fiserv.commercehub.androidttp.ui.common.DefaultButton
import com.fiserv.commercehub.androidttp.ui.main.screen.viewModel.ChoiceScreenViewModel
import com.fiserv.commercehub.androidttp.ui.main.screen.viewmodel.ChoiceScreenViewModel
import com.fiserv.commercehub.androidttp.ui.theme.AndroidTapToPayDemoTheme


/**
 * Initial choice screen that allows users to select between Test and Demo modes.
 * Displays app logo, description, navigation buttons, and version information.
 *
 * @param navigateToDemoHome: function which navigates to demo mode home screen
 * @param navigateToTestLanding: function which navigates to test mode landing screen
 */
@Composable
fun ChoiceScreen(
    navigateToDemoHome: () -> Unit,
    navigateToTestLanding: () -> Unit,
    viewModel: ChoiceScreenViewModel = viewModel()
) {

    var context = LocalContext.current
    var textVersion = remember { mutableStateOf("") }

    textVersion.value=stringResource(R.string.version_name) +" "+BuildConfig.VERSION_NAME.toString() + stringResource(R.string.version_code) +" "+ BuildConfig.VERSION_CODE.toString()

    var textMagicCubeVersion = remember { mutableStateOf("") }
    textMagicCubeVersion.value=viewModel.getMagicCubeSDKVersion()

    var textTTPCubeVersion = remember { mutableStateOf("") }
    textTTPCubeVersion.value=stringResource(R.string.ttp_version_name) +" "+viewModel.getTTPSDKVersion()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp).systemBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App logo and description
        Spacer(modifier = Modifier.height(50.dp))
        Image(
            modifier = Modifier.height(80.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null // decorative element
        )
        Text(
            text = stringResource(R.string.desc),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.weight(1f))


        // Navigation buttons
        Spacer(modifier = Modifier.weight(1f))
        DefaultButton(stringResource(R.string.test_mode), onClick = navigateToTestLanding)
        Spacer(modifier = Modifier.height(10.dp))

        DefaultButton(stringResource(R.string.demo_mode), onClick ={

            navigateToDemoHome( )
        })
        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = textVersion.value,
                fontSize = 12.sp,
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text =   textMagicCubeVersion.value,
                fontSize = 12.sp,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text =   textTTPCubeVersion.value,
                fontSize = 12.sp,
            )
        }
    }
}


/**
 * Preview testing ChoiceScreen layout
 * Shows screen layout with default theme and mock navigation.
 */
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    AndroidTapToPayDemoTheme(useSystemUiController = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ChoiceScreen(
                navigateToDemoHome = {},
                navigateToTestLanding = {},
            )
        }
    }
}

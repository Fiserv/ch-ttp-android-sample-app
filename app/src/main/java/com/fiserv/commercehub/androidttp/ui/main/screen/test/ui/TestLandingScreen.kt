package com.fiserv.commercehub.androidttp.ui.main.screen.test.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiserv.commercehub.androidttp.BuildConfig
import com.fiserv.commercehub.androidttp.R
import com.fiserv.commercehub.androidttp.ui.common.DefaultButton
import com.fiserv.commercehub.androidttp.ui.theme.AndroidTapToPayDemoTheme

/**
 * Test landing screen that provides SDK initialization configuration.
 * Configures API credentials, merchant details, and server details.
 * Displays version information and provides navigation to SDK testing interface.
 *
 * @param popBackStack: function which navigates back to previous screen
 * @param navigateToTestSDKMain: function which navigates to test screen with input configuration
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestLandingScreen(
    popBackStack: () -> Unit,
    navigateToTestSDKMain: (String, String, String, String, String, String) -> Unit,
) {

    // State holders for fields
    var textApiKey = remember { mutableStateOf("") }
    var textSecreteKey = remember { mutableStateOf("") }
    var textMerchantID = remember { mutableStateOf("") }
    var textTerminalID = remember { mutableStateOf("") }
    var textPPID = remember { mutableStateOf("") }
    var textHostPort = remember { mutableStateOf(BuildConfig.host_port) }
    var textVersion = remember { mutableStateOf("") }

    // Initialization from BuildConfig values
    textVersion.value =
        "VERSION NAME: " + BuildConfig.VERSION_NAME.toString() + ", VERSION CODE: " + BuildConfig.VERSION_CODE.toString()
    textApiKey.value = BuildConfig.apiKey.toString()
    textSecreteKey.value = BuildConfig.apiSecret
    textPPID.value = BuildConfig.ppid
    textMerchantID.value = BuildConfig.mid
    textTerminalID.value = BuildConfig.tid

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        // Top app bar with navigation and title
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
            title = {
                Text(
                    text = stringResource(R.string.init_sdk).toString(),
                    textAlign = TextAlign.Center,
                    color = White
                )
            },
            navigationIcon = {
                IconButton(onClick = popBackStack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "menu items",
                        tint = White
                    )
                }
            },
            actions = {

            }
        )
        // Main content card; input fields for API keys, merchant details, and server details
        Card(
            border = BorderStroke(1.dp, Color.LightGray),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),

                ) {


                Spacer(modifier = Modifier.height(5.dp))
                OutlinedTextField(
                    value = textApiKey.value,
                    onValueChange = { textApiKey.value = it },
                    label = { Text(stringResource(R.string.api_key)) },
                    modifier = Modifier.fillMaxWidth(), textStyle = TextStyle(color = Color.Black),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        disabledContainerColor = Color.Gray,
                        errorContainerColor = Color.Red,
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))


                OutlinedTextField(
                    value = textSecreteKey.value,
                    onValueChange = { textSecreteKey.value = it },
                    label = { Text(stringResource(R.string.secrete_key)) },
                    modifier = Modifier.fillMaxWidth(), textStyle = TextStyle(color = Color.Black),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        disabledContainerColor = Color.Gray,
                        errorContainerColor = Color.Red,
                    )
                )



                Spacer(modifier = Modifier.height(16.dp))



                OutlinedTextField(
                    value = textMerchantID.value,
                    onValueChange = { textMerchantID.value = it },
                    label = { Text(stringResource(R.string.merchant_id)) },
                    modifier = Modifier.fillMaxWidth(), textStyle = TextStyle(color = Color.Black),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        disabledContainerColor = Color.Gray,
                        errorContainerColor = Color.Red,
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))



                /*  OutlinedTextField(
                      value = textMerchantName.value,
                      onValueChange = { textMerchantName.value = it },
                      label = { Text(stringResource(R.string.merchant_name)) },
                      modifier = Modifier.fillMaxWidth(), textStyle = TextStyle(color = Color.Black),
                  )
                  Spacer(modifier = Modifier.height(16.dp))*/


                OutlinedTextField(
                    value = textTerminalID.value,
                    onValueChange = { textTerminalID.value = it },
                    label = { Text(stringResource(R.string.terminal_id)) },
                    modifier = Modifier.fillMaxWidth(), textStyle = TextStyle(color = Color.Black),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        disabledContainerColor = Color.Gray,
                        errorContainerColor = Color.Red,
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))


                OutlinedTextField(
                    value = textPPID.value,
                    onValueChange = { textPPID.value = it },
                    label = { Text(stringResource(R.string.ppid)) },
                    modifier = Modifier.fillMaxWidth(), textStyle = TextStyle(color = Color.Black),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        disabledContainerColor = Color.Gray,
                        errorContainerColor = Color.Red,
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))


                OutlinedTextField(
                    value = textHostPort.value,
                    onValueChange = { textHostPort.value = it },
                    label = { Text(stringResource(R.string.host_port)) },
                    modifier = Modifier.fillMaxWidth(), textStyle = TextStyle(color = Color.Black),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        disabledContainerColor = Color.Gray,
                        errorContainerColor = Color.Red,
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                DefaultButton(stringResource(R.string.init_sdk), onClick = {

                    navigateToTestSDKMain(
                        textApiKey.value,
                        textSecreteKey.value,
                        textMerchantID.value,
                        textTerminalID.value,
                        textPPID.value,
                        textHostPort.value
                    )

                })
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = textVersion.value,
                        fontSize = 12.sp,
                    )
                }

            }
        }
    }


}

/**
 * Preview testing the TestLandingScreen layout.
 * Shows screen with default theme and mock navigation callbacks.
 */
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    AndroidTapToPayDemoTheme(useSystemUiController = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TestLandingScreen(
                popBackStack = {},
                navigateToTestSDKMain = { _, _, _, _, _, _ -> }
            )
        }
    }
}

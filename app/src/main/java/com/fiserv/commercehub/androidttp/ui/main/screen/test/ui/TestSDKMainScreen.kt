package com.fiserv.commercehub.androidttp.ui.main.screen.test.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiserv.commercehub.androidttp.BuildConfig
import com.fiserv.commercehub.androidttp.R
import com.fiserv.commercehub.androidttp.ui.common.CustomViewLogConsole
import com.fiserv.commercehub.androidttp.ui.common.DefaultButton
import com.fiserv.commercehub.androidttp.ui.main.screen.test.viewModel.TestSDKMainViewModel
import com.fiserv.commercehub.androidttp.ui.theme.AndroidTapToPayDemoTheme
import com.fiserv.commercehub.ttp.provider.constants.Currency

/**
 * Test SDK screen providing interface for testing various TTP SDK functionalities.
 * Features include SDK initialization, payment processing, refunds, card tokenization, and transaction management.
 *
 * @param popBackStack: function which navigates back to previous screen
 * @param apiKey: API key for SDK initialization
 * @param secreteKey: secret key for SDK authentication
 * @param merchantID: merchant identifier
 * @param terminalID: terminal identifier
 * @param pPID: product Package ID
 * @param hostPort: server host and port configuration
 * @param viewModel: viewModel instance for managing SDK operations
 * @param popUpToSplash: function which navigates to splash screen
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationGraphicsApi::class)
@Composable
fun TestSDKMainScreen(
    popBackStack: () -> Unit,
    apiKEy: String,
    secreteKey: String,
    merchantID: String,
    terminalID: String,
    pPID: String,
    hostPort: String,
    viewModel: TestSDKMainViewModel = viewModel(), popUpToSplash: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current


    var context = LocalContext.current
    // Field state holders
    var textApiKey = remember { mutableStateOf(apiKEy) }
    var textSecreteKey = remember { mutableStateOf(secreteKey) }
    var textMerchantID = remember { mutableStateOf(merchantID) }
    var textTerminalID = remember { mutableStateOf(terminalID) }
    var textPPID = remember { mutableStateOf(pPID) }
    var textHostPort = remember { mutableStateOf(hostPort) }

    var payTransactionID = remember { mutableStateOf("") }
    var payOrderID = remember { mutableStateOf("") }
    // var paymentTokenTextExpYear = remember { mutableStateOf("") }
    // var paymentToken = remember { mutableStateOf("") }
    //var paymentTokenTextExpMonth = remember { mutableStateOf("") }


    // Card tokenize
    var tokenizeTransactionId = remember { mutableStateOf("") }
    var tokenizeOrderId = remember { mutableStateOf("") }

    // Card  verification
    var verificationTransactionId = remember { mutableStateOf("") }
    var verificationOrderId = remember { mutableStateOf("") }
    val isMethodCalled = rememberSaveable { mutableStateOf(false) }
    val isChannelMethodCalled = rememberSaveable { mutableStateOf(false) }

    // DisposableEffect for SDK initialization on resume
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->

            if (event == Lifecycle.Event.ON_RESUME) {

                if (!isChannelMethodCalled.value)
                {
                    viewModel.initChannel()
                    isChannelMethodCalled.value=true
                }

                if (!isMethodCalled.value) {
                    viewModel.initViewModel(
                        context,
                        textApiKey.value,
                        textSecreteKey.value,
                        BuildConfig.env,
                        Currency.USD.toString(),
                        textMerchantID.value,
                        textTerminalID.value,
                        textPPID.value,
                        textHostPort.value
                    )
                    isMethodCalled.value = true
                }



            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    val radioOptionsSale = listOf(
        stringResource(R.string.capture),
        stringResource(R.string.auth),
        stringResource(R.string.sale)
    )
    val (selectedOptionSale, onOptionSelectedSale) = remember { mutableStateOf(radioOptionsSale[2]) }


    var switchCheckedState = remember { mutableStateOf(false) }


    val radioOptionsRefund = listOf(
        stringResource(R.string.tagged),
        stringResource(R.string.open),

        )
    val (selectedOptionRefund, onOptionSelectedRefund) = remember {
        mutableStateOf(
            radioOptionsRefund[0]
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // Top app bar with navigation and title
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
            title = {
                Text(
                    text = stringResource(R.string.testSDK).toString(),
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

                IconButton(onClick = {
                    if (viewModel.isLogPageVisible.value) {
                        viewModel.disableLogPage()
                    } else {
                        viewModel.visibleLogPage()
                    }

                }) {
                    Image(
                        painter = painterResource(R.drawable.log_icon),
                        modifier = Modifier
                            .height(30.dp)
                            .width(30.dp),
                        contentDescription = "Action console"
                    )
                }
            }
        )


        // Main scrollable content section
        Column(
            modifier = Modifier
                .weight(if (viewModel.isLogPageVisible.collectAsState().value == true) 1f else 2f)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {

            Card(
                border = BorderStroke(1.dp, Color.LightGray),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
            ) {

                // SDK re-initialization
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(16.dp),

                    ) {

                    if (viewModel.isLoadingInit.collectAsState().value == true) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Text(
                        text = stringResource(R.string.re_init_sdk),
                        fontSize = 15.sp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(color = Color.Black)
                    ) { }
                    Spacer(modifier = Modifier.height(10.dp))
                    DefaultButton(stringResource(R.string.re_init_sdk), onClick = {  viewModel.initViewModel(
                        context,
                        textApiKey.value,
                        textSecreteKey.value,
                        BuildConfig.env,
                        Currency.USD.toString(),
                        textMerchantID.value,
                        textTerminalID.value,
                        textPPID.value,
                        textHostPort.value
                    ) }
                    )
                }


            }

            // Inquiry
            Card(
                border = BorderStroke(1.dp, Color.LightGray),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
            ) {

                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(16.dp),

                    ) {

                    if (viewModel.isLoadingInquiry.collectAsState().value == true) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Text(
                        text = stringResource(R.string.inquiry),
                        fontSize = 15.sp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(color = Color.Black)
                    ) { }
                    Spacer(modifier = Modifier.height(10.dp))

                    Spacer(modifier = Modifier.height(5.dp))
                    OutlinedTextField(
                        value = viewModel.inquiryTransactionId.value.toString(),
                        onValueChange = { viewModel.inquiryTransactionId.value = it },
                        label = { Text(stringResource(R.string.reference_transactionId)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = viewModel.inquiryReferenceMerchantOrderId.value,
                        onValueChange = { viewModel.inquiryReferenceMerchantOrderId.value = it },
                        label = { Text(stringResource(R.string.reference_merchant_order_id)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = viewModel.inquiryReferenceOrderId.value,
                        onValueChange = { viewModel.inquiryReferenceOrderId.value = it },
                        label = { Text(stringResource(R.string.reference_order_id)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DefaultButton(stringResource(R.string.inquiry), onClick = {

                        viewModel.inquiryTransaction(context)
                        // navigateToDemoHome( )
                    })
                }


            }


            // Void
            Card(
                border = BorderStroke(1.dp, Color.LightGray),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
            ) {

                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(16.dp),

                    ) {

                    if (viewModel.isLoadingVoid.collectAsState().value == true) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Text(
                        text = stringResource(R.string.void_text),
                        fontSize = 15.sp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(color = Color.Black)
                    ) { }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = viewModel.voidAmount.value.toString(),
                        onValueChange = { viewModel.voidAmount.value = it.toString() },
                        label = { Text(stringResource(R.string.void_amount)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = viewModel.voidTransactionId.value,
                        onValueChange = { viewModel.voidTransactionId.value = it },
                        label = { Text(stringResource(R.string.reference_transactionId_void)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    DefaultButton(stringResource(R.string.void_transaction), onClick = {

                        viewModel.voidTransaction(context)
                        // navigateToDemoHome( )
                    })
                }


            }

            // Accept TTP Payment
            Card(
                border = BorderStroke(1.dp, Color.LightGray),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
            ) {


                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(16.dp),

                    ) {

                    if (viewModel.isLoadingPay.collectAsState().value == true) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Text(
                        text = stringResource(R.string.accept_a_ttp_payment),
                        fontSize = 15.sp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(color = Color.Black)
                    ) { }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = viewModel.payAmount.value.toString(),
                        onValueChange = { viewModel.payAmount.value = it.toString() },
                        label = { Text(stringResource(R.string.ttp_amount)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = payTransactionID.value,
                        onValueChange = { payTransactionID.value = it },
                        label = { Text(stringResource(R.string.your_trans_id_ttp)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = payOrderID.value,
                        onValueChange = { payOrderID.value = it },
                        label = { Text(stringResource(R.string.your_order_id_ttp)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = viewModel.payReferenceTransactionId.value,
                        onValueChange = { viewModel.payReferenceTransactionId.value = it },
                        label = { Text(stringResource(R.string.reference_trans_id_ttp)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = viewModel.payReferenceOrder.value,
                        onValueChange = { viewModel.payReferenceOrder.value = it },
                        label = { Text(stringResource(R.string.reference_order_id_ttp)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = viewModel.tokenData.value,
                        onValueChange = { viewModel.tokenData.value = it },
                        label = { Text(stringResource(R.string.payment_token_ttp)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = viewModel.tokenExpMonth.value,
                        onValueChange = { viewModel.tokenExpMonth.value = it },
                        label = { Text(stringResource(R.string.payment_token_exp_month)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = viewModel.tokenExpYear.value,
                        onValueChange = { viewModel.tokenExpYear.value = it },
                        label = { Text(stringResource(R.string.payment_token_exp_year)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Column {
                        radioOptionsSale.forEach { text ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (text == selectedOptionSale),
                                        onClick = {
                                            onOptionSelectedSale(text)
                                        }
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                RadioButton(
                                    selected = (text == selectedOptionSale),
                                    onClick = { onOptionSelectedSale(text) }
                                )
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.bodyMedium.merge(),
                                    modifier = Modifier.padding(start = 16.dp)
                                )


                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = stringResource(R.string.tokenize_card),
                            fontSize = 15.sp,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = switchCheckedState.value,
                            onCheckedChange = { switchCheckedState.value = it })

                    }
                    DefaultButton(stringResource(R.string.pay), onClick = {

                        viewModel.onPay(
                            context,
                            payTransactionID.value,
                            payOrderID.value,
                            selectedOptionSale,
                            switchCheckedState.value,
                            viewModel.tokenData.value,
                            viewModel.tokenExpYear.value,
                            viewModel.tokenExpMonth.value
                        )

                        viewModel.tokenData.value=""
                        viewModel.tokenExpYear.value=""
                        viewModel.tokenExpMonth.value=""
                        // navigateToDemoHome( )
                    })
                }


            }


            // Refund
            Card(
                border = BorderStroke(1.dp, Color.LightGray),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
            ) {

                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(16.dp),

                    ) {

                    if (viewModel.isLoadingRefund.collectAsState().value == true) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Text(
                        text = stringResource(R.string.refund),
                        fontSize = 15.sp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(color = Color.Black)
                    ) { }
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = viewModel.refundPayAmount.value.toString(),
                        onValueChange = { viewModel.refundPayAmount.value = it.toString() },
                        label = { Text(stringResource(R.string.refund_amount)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))


                    OutlinedTextField(
                        value = viewModel.refundTransactionId.value,
                        onValueChange = { viewModel.refundTransactionId.value = it },
                        label = { Text(stringResource(R.string.refund_your_tran_id)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = viewModel.refundOrderId.value,
                        onValueChange = { viewModel.refundOrderId.value = it },
                        label = { Text(stringResource(R.string.refund_your_order_id)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = viewModel.refundReferenceTransactionId.value,
                        onValueChange = { viewModel.refundReferenceTransactionId.value = it },
                        label = { Text(stringResource(R.string.refund_reference_trans_id)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Column {
                        radioOptionsRefund.forEach { text ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (text == selectedOptionRefund),
                                        onClick = {
                                            onOptionSelectedRefund(text)
                                        }
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (text == selectedOptionRefund),
                                    onClick = { onOptionSelectedRefund(text) }
                                )
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.bodySmall.merge(),
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }

                    DefaultButton(stringResource(R.string.refund), onClick = {
                        viewModel.onRefund(context, selectedOptionRefund)
                    })
                }


            }


            // Card Tokenization
            Card(
                border = BorderStroke(1.dp, Color.LightGray),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
            ) {

                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(16.dp),

                    ) {

                    if (viewModel.isLoadingTokenize.collectAsState().value == true) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Text(
                        text = stringResource(R.string.card_tokenization),
                        fontSize = 15.sp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(color = Color.Black)
                    ) { }
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tokenizeTransactionId.value,
                        onValueChange = { tokenizeTransactionId.value = it },
                        label = { Text(stringResource(R.string.card_token_your_trans_id)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))


                    OutlinedTextField(
                        value = tokenizeOrderId.value,
                        onValueChange = { tokenizeOrderId.value = it },
                        label = { Text(stringResource(R.string.card_token_your_order_id)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    DefaultButton(stringResource(R.string.card_token_tokenize_card), onClick = {

                        viewModel.onTokenizeCardClick(
                            context,
                            tokenizeTransactionId.value,
                            tokenizeOrderId.value
                        )
                        // navigateToDemoHome( )
                    })
                }


            }


            //Card Verification
            Card(
                border = BorderStroke(1.dp, Color.LightGray),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
            ) {

                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(16.dp),

                    ) {

                    if (viewModel.isLoadingVerifyCard.collectAsState().value == true) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Text(
                        text = stringResource(R.string.card_verification),
                        fontSize = 15.sp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(color = Color.Black)
                    ) { }
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = verificationTransactionId.value,
                        onValueChange = { verificationTransactionId.value = it },
                        label = { Text(stringResource(R.string.card_your_trans_id)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))


                    OutlinedTextField(
                        value = verificationOrderId.value,
                        onValueChange = { verificationOrderId.value = it },
                        label = { Text(stringResource(R.string.card_your_order_id)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = Color.Gray,
                            errorContainerColor = Color.Red,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DefaultButton(stringResource(R.string.verify_card), onClick = {

                        viewModel.onAccountVerificationCardClick(
                            context,
                            verificationTransactionId.value,
                            verificationOrderId.value
                        )

                    })
                }
            }
        }

        if (!viewModel.isNFCSupportedAlert.collectAsState().value)
        {
            isMethodCalled.value=false
            AlertDialog(
                onDismissRequest = {   },
                confirmButton = {
                    Button(onClick = popBackStack)
                    { Text(stringResource(R.string.ok), color = White) }
                },


                title = { Text(text = stringResource(R.string.nfc_not_support), color = Color.Black, fontSize = 12.sp) },

                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                containerColor = White,
                iconContentColor = Color.Red,
                titleContentColor = Color.Black,
                textContentColor = Color.DarkGray,
                tonalElevation = 8.dp,
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            )
        }
        if (!viewModel.isNFCEnabledAlert.collectAsState().value)
        {
            isMethodCalled.value=false
            AlertDialog(
                onDismissRequest = {  viewModel.disableNFCAlert() },
                confirmButton = {
                    Button(onClick = {
                        viewModel.disableNFCAlert()
                        val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                        context.startActivity(intent)
                    })
                    { Text(stringResource(R.string.settings), color = White) }
                },
                dismissButton = {
                    TextButton(onClick = popBackStack ) {
                        Text(stringResource(R.string.dismiss))
                    }
                },

                title = { Text(text = stringResource(R.string.nfc_disabled), color = Color.Black, fontSize = 12.sp) },

                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                containerColor = White,
                iconContentColor = Color.Red,
                titleContentColor = Color.Black,
                textContentColor = Color.DarkGray,
                tonalElevation = 8.dp,
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            )
        }


        if (viewModel.isPaymentSuccessAlert.collectAsState().value)
        {
            LaunchedEffect(Unit) {
                viewModel.dismiss()
            }

            Dialog(onDismissRequest = {  viewModel.dismiss() }) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAnimatedVectorPainter(animatedImageVector = AnimatedImageVector.animatedVectorResource(R.drawable.avd_done),
                                atEnd = true),
                            contentDescription = null,
                            modifier =
                                Modifier
                                    .height(100.dp)
                                    .width(100.dp)
                                    .padding(20.dp),

                            )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.payment_done))
                    }
                }
            }




        }

        // Log console (if visible)
        if (viewModel.isLogPageVisible.collectAsState().value == true) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(color = Color.LightGray),
            ) {
                CustomViewLogConsole(viewModel.logData, onClickDisable = {
                    viewModel.disableLogPage()
                })
            }


        }

    }


}

/**
 * Preview testing the TestSDKMainScreen layout.
 * Shows screen layout with default theme and mock data.
 */
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    AndroidTapToPayDemoTheme(useSystemUiController = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TestSDKMainScreen(
                popBackStack = {},
                apiKEy = "",
                secreteKey = "",
                merchantID = "",
                terminalID = "",
                pPID = "",
                hostPort = "", popUpToSplash = {}
            )
        }
    }
}

package com.fiserv.commercehub.androidttp.ui.main.screen.demo.ui


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiserv.commercehub.androidttp.R
import com.fiserv.commercehub.androidttp.data.model.Product
import com.fiserv.commercehub.androidttp.ui.common.CustomButton
import com.fiserv.commercehub.androidttp.ui.common.CustomViewLogConsole
import com.fiserv.commercehub.androidttp.ui.common.DefaultButton
import com.fiserv.commercehub.androidttp.ui.main.screen.demo.adapter.CheckOutProductListView
import com.fiserv.commercehub.androidttp.ui.main.screen.demo.adapter.LogsListView
import com.fiserv.commercehub.androidttp.ui.main.screen.demo.adapter.calculationQtyAndItemPrice
import com.fiserv.commercehub.androidttp.ui.main.screen.demo.viewModel.CheckOutViewModel
import com.fiserv.commercehub.androidttp.ui.theme.AndroidTapToPayDemoTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationGraphicsApi::class)
@Composable
fun CheckOutScreen(
    popBackStack: () -> Unit,
    productData: String,
    popUpToChoiceScreen: () -> Unit,
    viewModel: CheckOutViewModel = viewModel(),
) {

    var context = LocalContext.current
    val listType: Type? = object : TypeToken<List<Product>>() {}.type
    Log.e("Product Data", productData)
    val products = Gson().fromJson(productData, listType) as List<Product>

    LaunchedEffect(Unit) {
        viewModel.initViewModel()
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
            title = {
                Text(
                    text = stringResource(R.string.Checkout).toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = popBackStack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "menu items",
                        tint = Color.White
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
        if (viewModel.isCheckOutPage.collectAsState().value) {

            CheckOutProductListView(products)
            Column()
            {
                Row(
                    // for our row we are adding modifier
                    // to set padding from all sides.
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(

                        text = stringResource(R.string.items),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )

                    Text(
                        text = stringResource(R.string.dollar_sign) + calculateProductCost(products).toString(),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(

                        text = stringResource(R.string.delivery),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )

                    Text(
                        text = stringResource(R.string.dollar_sign) + calculateProductDeliveryCharge(
                            calculateProductCost(products)
                        ).toString(),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(

                        text = stringResource(R.string.total),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )

                    Text(
                        text = stringResource(R.string.dollar_sign) + calculateTotalCost(products).toString(),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Box(modifier = Modifier.padding(10.dp)) {
                DefaultButton(stringResource(id = R.string.proceed), onClick = {
                    viewModel.onPay(context, calculateTotalCost(products))
                })
            }
        } else {


            if (viewModel.isPaymentSuccessAlert.collectAsState().value) {
                LaunchedEffect(Unit) {
                    viewModel.dismiss()
                }

                Dialog(onDismissRequest = { viewModel.dismiss() }) {
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
                                painter = rememberAnimatedVectorPainter(
                                    animatedImageVector = AnimatedImageVector.animatedVectorResource(
                                        R.drawable.avd_done
                                    ),
                                    atEnd = true
                                ),
                                contentDescription = null,
                                modifier =
                                Modifier
                                    .height(100.dp)
                                    .width(100.dp)
                                    .padding(20.dp),

                                )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Payment Completed Successfully!")
                        }
                    }
                }


            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .weight(if (viewModel.isLogPageVisible.collectAsState().value == true) 1f else 2f),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {

                if (viewModel.isLoadingRefund.collectAsState().value == true) {
                    Text(
                        text = stringResource(R.string.refund_process),
                        fontSize = 15.sp,
                    )
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

                }

                if (viewModel.isLoadingVoid.collectAsState().value == true) {
                    Text(
                        text = stringResource(R.string.cancel_process),
                        fontSize = 15.sp,
                    )
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

                }

                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    modifier = Modifier.height(130.dp),
                    painter = painterResource(id = R.drawable.order_completed),
                    contentDescription = null // decorative element
                )
                Text(
                    text = stringResource(R.string.payment_received),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),


                    ) {

                    Row {
                        Text(
                            text = stringResource(R.string.payment_ref_number),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        Text(
                            text = viewModel.transactionId.value,
                            fontSize = 15.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row {
                        Text(
                            text = stringResource(R.string.aid),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(top = 10.dp)
                        )

                        Text(
                            text = viewModel.orderId.value,
                            fontSize = 15.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row {
                        Text(
                            text = stringResource(R.string.transaction_date),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(top = 10.dp)
                        )

                        Text(
                            text = viewModel.getCurrentDateTime(),
                            fontSize = 15.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {

                        CustomButton(stringResource(R.string.refund), onClick = {
                            viewModel.onRefund(context)
                        })
                        Spacer(modifier = Modifier.weight(1f))
                        CustomButton(stringResource(R.string.cancel), onClick = {
                            viewModel.voidTransaction(context)
                        })
                    }


                    Spacer(modifier = Modifier.height(10.dp))
                    DefaultButton(stringResource(R.string.return_home), onClick = popUpToChoiceScreen
                    )
                }
            }
        }


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


fun calculateProductCost(products: List<Product>): Double {

    return products.foldRight(0.0) { product, total ->
        total + calculationQtyAndItemPrice(
            product.qty,
            product.productCost!!
        )
    }.roundTo2DecimalPlaces()
}

fun calculateProductDeliveryCharge(cost: Double): Double {

    return ((cost * 0.02) / 100).roundTo2DecimalPlaces()
}


fun calculateTotalCost(products: List<Product>): Double {
    return calculateProductCost(products).let { it + calculateProductDeliveryCharge(it) }
        .let { it.roundTo2DecimalPlaces() }
}

fun Double.roundTo2DecimalPlaces(): Double {
    return String.format("%.2f", this).toDouble()
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    AndroidTapToPayDemoTheme(useSystemUiController = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CheckOutScreen(
                popBackStack = {}, "{}",
                popUpToChoiceScreen = {}
            )
        }
    }
}











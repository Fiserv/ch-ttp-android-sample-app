package com.fiserv.commercehub.androidttp.ui.main.screen.demo.ui


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiserv.commercehub.androidttp.R
import com.fiserv.commercehub.androidttp.ui.common.CustomViewLogConsole
import com.fiserv.commercehub.androidttp.ui.common.DefaultButton
import com.fiserv.commercehub.androidttp.ui.main.screen.demo.adapter.LogsListView
import com.fiserv.commercehub.androidttp.ui.main.screen.demo.adapter.ProductListView
import com.fiserv.commercehub.androidttp.ui.main.screen.demo.viewModel.ProductViewModel
import com.fiserv.commercehub.androidttp.ui.theme.AndroidTapToPayDemoTheme
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoHomeScreen(

    viewModel: ProductViewModel = viewModel(),
    navigateToCheckOut: (String) -> Unit,
    popBackStack: () -> Unit,
) {
    val context = LocalContext.current
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
                    text = stringResource(R.string.products).toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton( onClick = popBackStack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = viewModel.log.value,
                fontSize = 15.sp,
                modifier = Modifier.padding(10.dp),
            )
            if (viewModel.isLoadingInit.collectAsState().value) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        ProductListView(viewModel)
        if (viewModel.cartSize.collectAsState().value == 0) {

        } else {
            Box(modifier = Modifier.padding(10.dp)) {
                DefaultButton(stringResource(id = R.string.Checkout),
                    onClick = {
                        if(!viewModel.isLoadingInit.value) {
                            viewModel.updateCart()
                                .let { Gson().toJson(it) }
                                .apply {  navigateToCheckOut(this) }
                        } else {
                            Toast.makeText(context, "Wait for 'Ready to Accept Payment' message", Toast.LENGTH_LONG).show()
                        }

                    }
                )
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

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    AndroidTapToPayDemoTheme(useSystemUiController = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DemoHomeScreen(
                viewModel(), navigateToCheckOut = {},popBackStack = {},
            )
        }
    }
}




package com.fiserv.commercehub.androidttp.ui.main.screen.demo.adapter

import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.fiserv.commercehub.androidttp.R
import com.fiserv.commercehub.androidttp.data.model.Product
import com.fiserv.commercehub.androidttp.ui.main.screen.demo.viewModel.ProductViewModel
import com.fiserv.commercehub.androidttp.ui.theme.AndroidTapToPayDemoTheme


@Composable
fun ProductListView(
    viewModel: ProductViewModel,
    listState: LazyListState = rememberLazyListState(),
) {

    var context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }
    var text = remember { mutableStateOf("") }

    var selectedProduct: MutableState<Product> = remember { mutableStateOf(value = Product()) }

    LazyColumn(modifier = Modifier.padding(16.dp), state = listState) {
        items(viewModel.products, key = { listItem ->
            listItem.id
        }) { product ->
            ListItem(product, onAddToCart = { viewModel.updateProductQty(product, true) }
                , onDelete = { viewModel.updateProductQty(product, false) }
                , onEditCost = {
                    openDialog.value = true
                    selectedProduct.value = it
                }
            )
        }
    }

    if(openDialog.value) {
        text.value = selectedProduct.value.productCost.toString()

        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            confirmButton = {
                Button(onClick = {
                    if (!TextUtils.isEmpty(text.value)) {
                        viewModel.updateProductCost(selectedProduct.value, text.value.toString())
                        openDialog.value = false
                    }
                })
                { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text(stringResource(R.string.dismiss))
                }
            },

            title = { Text(text = stringResource(R.string.update_product), color = Color.Black, fontSize = 12.sp) },

            text = {
                Column() {
                    OutlinedTextField(
                        value = text.value.toString(),
                        onValueChange = { text.value = it.toString() },
                        isError = text.value.isNotEmpty(),
                        textStyle = TextStyle(color = Color.Black)
                    )
                }
            },

            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            containerColor = White,
            iconContentColor = Color.Red,
            titleContentColor = Color.Black,
            textContentColor = Color.DarkGray,
            tonalElevation = 8.dp,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
        )
    }
    LaunchedEffect(Unit) {
        viewModel.initFiservTTPSession(context)
    }
}


@Composable
fun ListItem(
    product: Product,
    onAddToCart: (Product) -> Unit,
    onDelete: (Product) -> Unit,
    onEditCost: (Product) -> Unit,
) {
    Card(
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp, 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            when (product.type) {
                1 -> Image(
                    painter = painterResource(id = R.drawable.ic_book),
                    contentDescription = "Book",
                    modifier = Modifier
                        .height(60.dp)
                        .width(60.dp)
                )
                2 -> Image(
                    painter = painterResource(id = R.drawable.ic_phone_case),
                    contentDescription = "Phone case",
                    modifier = Modifier
                        .height(60.dp)
                        .width(60.dp)
                )
                3 -> Image(
                    painter = painterResource(id = R.drawable.ic_phone),
                    contentDescription = "Phone",
                    modifier = Modifier
                        .height(60.dp)
                        .width(60.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = product.productName.toString(),
                    color = colorResource(R.color.text_bold),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.cost) + " $ " + product.productCost.toString(),
                        color = colorResource(R.color.text_bold),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    )
                    IconButton(
                        onClick = { onEditCost(product) },
                        modifier = Modifier
                            .size(25.dp, 25.dp))
                    {
                        Image(
                            painter = painterResource(id = R.drawable.edit),
                            contentDescription = stringResource(id = R.string.app_name),
                            modifier = Modifier
                                .size(10.dp, 10.dp)
                        )
                    }
                }
            }

            if (product.qty == 0) {
                IconButton(onClick = { onAddToCart(product) }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_cart),
                        contentDescription = stringResource(id = R.string.app_name),
                        modifier = Modifier.size(50.dp, 50.dp),
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(border = BorderStroke(width = 1.dp, Color.LightGray))
                ) {
                    IconButton(onClick = { onDelete(product) }) {
                        Image(
                            painter = painterResource(id = R.drawable.minus),
                            contentDescription = stringResource(id = R.string.app_name),
                            modifier = Modifier.size(15.dp, 15.dp),
                        )
                    }
                    Text(
                        text = product.qty.toString(),
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    IconButton(onClick = { onAddToCart(product) }) {
                        Image(
                            painter = painterResource(id = R.drawable.add),
                            contentDescription = stringResource(id = R.string.app_name),
                            modifier = Modifier.size(15.dp, 15.dp),
                        )
                    }
                }
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
            var viewModel = ProductViewModel()
            var product = Product(productName = "Smart phone", productCost = 906.73, qty = 0)
            ListItem(product, onAddToCart = {
                viewModel.updateProductQty(product, true)
            }, onDelete = {
                viewModel.updateProductQty(product, false)
            }, onEditCost = {
                viewModel.updateProductCost(product, "22.0")
            })
        }
    }
}

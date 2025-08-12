package com.fiserv.commercehub.androidttp.ui.main.screen.demo.adapter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiserv.commercehub.androidttp.R
import com.fiserv.commercehub.androidttp.data.model.Product
import com.fiserv.commercehub.androidttp.ui.main.screen.demo.viewModel.ProductViewModel

import com.fiserv.commercehub.androidttp.ui.theme.AndroidTapToPayDemoTheme

/**
 * Displays a scrollable list of products in checkout.
 *
 * @param product: list of Product items to be displayed
 * @param listState: state object to handle scrolling behavior; default rememberLazyListState
 */
@Suppress("FunctionName")
@Composable
fun CheckOutProductListView(
    product: List<Product>,
    listState: LazyListState = rememberLazyListState(),
) {

    LazyColumn(modifier = Modifier.padding(16.dp), state = listState) {
        items(product, key = { listItem ->
            listItem.id }) { product ->
            ShowListItem(product)
        }
    }
}

/**
 * Displays a product item card format with following details:
 * product image, name, cost per unit, quantity, total price.
 *
 * @param product: the Product object containing item details
 */
@Composable
fun ShowListItem(product: Product) {
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
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            // Display product image based on type
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
            // Set product details based on input
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.productName.toString(),
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = stringResource(R.string.cost) + " $" + product.productCost.toString(),
                    color = Color.Black,
                    fontSize = 14.sp
                )

                Text(
                    text = stringResource(R.string.qty) + ": " + product.qty.toString(),
                    color = Color.Black,
                    fontSize = 14.sp
                )

            }
            // Display total price based on quantity and cost
            Column{
                Spacer(modifier = Modifier
                    .height(20.dp)
                    .padding(10.dp))
                Text(
                    text = "$" + calculationQtyAndItemPrice(
                        product.qty,
                        product.productCost!!
                    ).toString(),
                    color = Color.Black,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * Calculates the total price for a product based on quantity and unit cost.
 *
 * @param qty: the quantity of the product
 * @param cost: the unit cost of the product
 * @return the total price as a Double
 */
fun calculationQtyAndItemPrice(qty: Int, cost: Double): Double {
    return qty * cost
}

/**
 * Preview testing the ShowListItem component with sample data.
 * Displays a single product item with mock data.
 */
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    AndroidTapToPayDemoTheme(useSystemUiController = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            var viewModel = ProductViewModel()
            var product = Product(productName = "Samsung Galaxy S23", productCost = 906.73, qty = 4)

            ShowListItem(product)
        }
    }
}

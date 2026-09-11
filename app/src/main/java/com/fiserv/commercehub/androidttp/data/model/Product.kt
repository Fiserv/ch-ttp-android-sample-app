package com.fiserv.commercehub.androidttp.data.model

import androidx.annotation.Keep

/**
 * Data class representing a product in the application.
 * @property id: the unique identifier for the product. Defaults to 0; no null.
 * @property productName: the name of the product. Can be null.
 * @property productCost: the cost of the product. Can be null.
 * @property qty: the quantity of the product. Defaults to 0; no null.
 * @property productImage: the URL or path to the product's image. Can be null.
 * @property type: the type/category of the product. Defaults to 1; no null.
 */
@Keep
data class Product(
    val id: Long = 0,
    val productName: String? = null,
    var productCost: Double? = null,
    var qty: Int = 0,
    val productImage: String?=null,
    val type:Int= 1
)

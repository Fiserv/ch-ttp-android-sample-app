package com.fiserv.commercehub.androidttp.data.model

import androidx.annotation.Keep


@Keep
data class Product(
    val id: Long = 0,
    val productName: String? = null,
    var productCost: Double? = null,
    var qty: Int = 0,
    val productImage: String?=null,
    val type:Int= 1
)

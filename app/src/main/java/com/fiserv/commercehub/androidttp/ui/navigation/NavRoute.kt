package com.fiserv.commercehub.androidttp.ui.navigation

sealed class NavRoute(val path: String) {

    object ChoiceScreen : NavRoute("choice")
    object DemoHome : NavRoute("demo_home") // Demo - Product List Page
    object TestLanding : NavRoute("test_landing")// Existing Test SDK Landing Page
    object CheckoutScreen : NavRoute("product_data") {
        val products = "productData"
    } // Equivalent to checkout page

    object TestSDKMainScreen : NavRoute("TestSDKMainScreen") {
        val apiKey = "apiKey"
        val secretKey = "secreteKey"
        val merchantID = "merchantID"
        val merchantName = "merchantName"
        val terminalID = "terminalID"
        val ppid = "ppid"
        val hostPort = "hostPort"
    }

    fun withArgs(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

    fun withArgsFormat(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach { arg ->
                append("/{$arg}")
            }
        }
    }
}



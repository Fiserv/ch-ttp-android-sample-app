package com.fiserv.commercehub.androidttp.ui.navigation

sealed class NavRoute(val path: String) {

    // Screen objects representing different pages in the app
    object ChoiceScreen : NavRoute("choice")
    object DemoHome : NavRoute("demo_home") // Demo - Product List Page
    object TestLanding : NavRoute("test_landing")// Existing Test SDK Landing Page
    object CheckoutScreen : NavRoute("product_data") {
        val products = "productData"
    } // Equivalent to checkout page

    // Main screen initialization with API keys and other parameters
    object TestSDKMainScreen : NavRoute("TestSDKMainScreen") {
        val apiKey = "apiKey"
        val secretKey = "secreteKey"
        val merchantID = "merchantID"
        val merchantName = "merchantName"
        val terminalID = "terminalID"
        val ppid = "ppid"
        val hostPort = "hostPort"
    }

    /**
     * Constructs route path with appended arguments.
     * Example: "base/arg1/arg2"
     *
     * @param args: arguments to append to base path
     * @return: complete route path with arguments
     */
    fun withArgs(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

    /**
     * Constructs route path with argument placeholders.
     * Example: "base/{arg1}/{arg2}"
     *
     * @param args: arguments to create placeholders for
     * @return: route path with argument placeholders
     */
    fun withArgsFormat(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach { arg ->
                append("/{$arg}")
            }
        }
    }
}



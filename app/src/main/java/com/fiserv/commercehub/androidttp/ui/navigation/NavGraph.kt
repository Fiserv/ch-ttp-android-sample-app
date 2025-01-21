package com.fiserv.commercehub.androidttp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fiserv.commercehub.androidttp.ui.main.screen.ChoiceScreen
import com.fiserv.commercehub.androidttp.ui.main.screen.demo.ui.CheckOutScreen
import com.fiserv.commercehub.androidttp.ui.main.screen.demo.ui.DemoHomeScreen
import com.fiserv.commercehub.androidttp.ui.main.screen.test.ui.TestLandingScreen
import com.fiserv.commercehub.androidttp.ui.main.screen.test.ui.TestSDKMainScreen


@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = NavRoute.ChoiceScreen.path
    ) {
        addChoiceScreen(navController, this)
        addDemoHomeScreen(navController, this)
        addCheckOutScreen(navController, this)
        addTestLandingScreen(navController, this)
        addTestSDKMainScreen(navController, this)
    }
}

private fun addChoiceScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
) {
    navGraphBuilder.composable(route = NavRoute.ChoiceScreen.path) {
        ChoiceScreen(
            navigateToTestLanding = { navController.navigate(NavRoute.TestLanding.path) },
            navigateToDemoHome = { navController.navigate(NavRoute.DemoHome.path) }
        )
    }
}

private fun addTestLandingScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
) {
    navGraphBuilder.composable(route = NavRoute.TestLanding.path) {
        TestLandingScreen(
            popBackStack = { navController.popBackStack() },
            navigateToTestSDKMain = { textApiKey, textSecreteKey, textMerchantID, textTerminalID, textPPID, textHostPort ->
                navController.navigate(
                    NavRoute.TestSDKMainScreen.withArgs(
                        textApiKey,
                        textSecreteKey,
                        textMerchantID,
                        textTerminalID,
                        textPPID,
                        textHostPort
                    )
                )
            },
        )
    }
}

private fun addDemoHomeScreen(
    navHostController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
) {
    navGraphBuilder.composable(route = NavRoute.DemoHome.path) {
        DemoHomeScreen(
            navigateToCheckOut = { navHostController.navigate(NavRoute.CheckoutScreen.withArgs(it.toString())) },
            popBackStack = { navHostController.popBackStack() }
        )
    }
}

private fun addCheckOutScreen(
    navHostController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
) {
    navGraphBuilder.composable(
        route = NavRoute.CheckoutScreen.withArgsFormat(NavRoute.CheckoutScreen.products),
        arguments = listOf(
            navArgument(NavRoute.CheckoutScreen.products) {
                type = NavType.StringType
                nullable = false
            }
        )
    ) { navBackStackEntry ->
        val args = navBackStackEntry.arguments
        CheckOutScreen(
            popBackStack = { navHostController.popBackStack() },
            productData = args!!.getString(NavRoute.CheckoutScreen.products)!!,
            popUpToChoiceScreen = { popUpToChoiceScreen(navHostController) },
        )
    }
}


private fun addTestSDKMainScreen(
    navHostController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
) {


    navGraphBuilder.composable(
        route = NavRoute.TestSDKMainScreen.withArgsFormat(
            NavRoute.TestSDKMainScreen.apiKey,
            NavRoute.TestSDKMainScreen.secretKey,
            NavRoute.TestSDKMainScreen.merchantID,
            NavRoute.TestSDKMainScreen.terminalID,
            NavRoute.TestSDKMainScreen.ppid,
            NavRoute.TestSDKMainScreen.hostPort
        ),
        arguments = listOf(
            navArgument(NavRoute.TestSDKMainScreen.apiKey) {
                type = NavType.StringType
                nullable = false
            }, navArgument(NavRoute.TestSDKMainScreen.secretKey) {
                type = NavType.StringType
                nullable = false
            }, navArgument(NavRoute.TestSDKMainScreen.merchantID) {
                type = NavType.StringType
                nullable = false
            },  navArgument(NavRoute.TestSDKMainScreen.terminalID) {
                type = NavType.StringType
                nullable = false
            }, navArgument(NavRoute.TestSDKMainScreen.ppid) {
                type = NavType.StringType
                nullable = false
            }, navArgument(NavRoute.TestSDKMainScreen.hostPort) {
                type = NavType.StringType
                nullable = false
            }


        )
    ) { navBackStackEntry ->

        val args = navBackStackEntry.arguments

        TestSDKMainScreen(
            popBackStack = { navHostController.popBackStack() },
            apiKEy = args?.getString(NavRoute.TestSDKMainScreen.apiKey) ?: "",
            secreteKey = args?.getString(NavRoute.TestSDKMainScreen.secretKey) ?: "",
            merchantID = args?.getString(NavRoute.TestSDKMainScreen.merchantID) ?: "",
            terminalID = args?.getString(NavRoute.TestSDKMainScreen.terminalID) ?: "",
            pPID = args?.getString(NavRoute.TestSDKMainScreen.ppid) ?: "",
            hostPort = args?.getString(NavRoute.TestSDKMainScreen.hostPort) ?: "",
            popUpToSplash = { popUpToChoiceScreen(navHostController) }
        )
    }
}

private fun popUpToChoiceScreen(navController: NavHostController) {
    navController.popBackStack(NavRoute.ChoiceScreen.path, inclusive = false)
}




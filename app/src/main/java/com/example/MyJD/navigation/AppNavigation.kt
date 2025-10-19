package com.example.MyJD.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.MyJD.ui.screen.HomeScreen
import com.example.MyJD.ui.screen.ChatScreen
import com.example.MyJD.ui.screen.MeScreen
import com.example.MyJD.ui.screen.PlaceholderScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToSearch = { query ->
                    navController.navigate("search/$query")
                },
                onNavigateToProduct = { productId ->
                    navController.navigate("product/$productId")
                },
                onNavigateToCart = {
                    navController.navigate("cart")
                },
                onNavigateToSupermarket = {
                    navController.navigate("supermarket")
                }
            )
        }
        
        composable("video") {
            PlaceholderScreen(
                title = "视频",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("chat") {
            ChatScreen(
                onNavigateToCart = {
                    navController.navigate("cart")
                },
                onNavigateToDetail = { messageId ->
                    navController.navigate("chat_detail/$messageId")
                }
            )
        }
        
        composable("cart") {
            com.example.MyJD.ui.screen.CartScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        
        composable("profile") {
            MeScreen(
                onNavigateToSettings = {
                    navController.navigate("settings")
                },
                onNavigateToAddress = {
                    navController.navigate("address")
                },
                onNavigateToChat = {
                    navController.navigate("chat")
                },
                onNavigateToOrderList = { orderType ->
                    navController.navigate("order_list/$orderType")
                }
            )
        }
        
        composable("search/{query}") { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            PlaceholderScreen(
                title = "搜索: $query",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("product/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            PlaceholderScreen(
                title = "商品详情",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("supermarket") {
            PlaceholderScreen(
                title = "京东超市",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("chat_detail/{messageId}") { backStackEntry ->
            val messageId = backStackEntry.arguments?.getString("messageId") ?: ""
            PlaceholderScreen(
                title = "消息详情",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("settings") {
            PlaceholderScreen(
                title = "设置",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("address") {
            PlaceholderScreen(
                title = "地址管理",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("order_list/{orderType}") { backStackEntry ->
            val orderType = backStackEntry.arguments?.getString("orderType") ?: ""
            PlaceholderScreen(
                title = "订单列表",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
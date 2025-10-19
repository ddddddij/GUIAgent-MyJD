package com.example.MyJD.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.MyJD.ui.screen.HomeScreen
import com.example.MyJD.ui.screen.ChatScreen
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
            PlaceholderScreen("视频")
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
            PlaceholderScreen("我的")
        }
        
        composable("search/{query}") { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            PlaceholderScreen("搜索: $query")
        }
        
        composable("product/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            PlaceholderScreen("商品详情")
        }
        
        composable("supermarket") {
            PlaceholderScreen("京东超市")
        }
        
        composable("chat_detail/{messageId}") { backStackEntry ->
            val messageId = backStackEntry.arguments?.getString("messageId") ?: ""
            PlaceholderScreen("消息详情")
        }
    }
}
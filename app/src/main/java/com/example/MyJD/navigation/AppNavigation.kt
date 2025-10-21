package com.example.MyJD.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.MyJD.ui.screen.HomeScreen
import com.example.MyJD.ui.screen.ChatScreen
import com.example.MyJD.ui.screen.MeScreen
import com.example.MyJD.ui.screen.PlaceholderScreen
import com.example.MyJD.ui.screen.ProductDetailScreen
import com.example.MyJD.ui.screen.OrderScreen
import com.example.MyJD.ui.screen.SettleScreen
import com.example.MyJD.ui.screen.PaymentSuccessScreen
import com.example.MyJD.ui.screen.SearchScreen
import com.example.MyJD.ui.screen.SearchResultScreen
import com.example.MyJD.ui.screen.MessageDetailScreen
import com.example.MyJD.ui.screen.MessageSettingScreen
import com.example.MyJD.ui.screen.ShopPageScreen

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
                },
                onNavigateToCheckout = {
                    navController.navigate("order_confirm?fromCart=true")
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
            SearchScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToSearchResult = { keyword ->
                    navController.navigate("search_result/$keyword")
                }
            )
        }
        
        composable("search_result/{keyword}") { backStackEntry ->
            val keyword = backStackEntry.arguments?.getString("keyword") ?: ""
            SearchResultScreen(
                keyword = keyword,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToProduct = { productId ->
                    navController.navigate("product/$productId")
                }
            )
        }
        
        composable("product/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                onBackClick = {
                    navController.popBackStack()
                },
                onCartClick = {
                    navController.navigate("cart")
                },
                onBuyNowClick = {
                    navController.navigate("order_confirm")
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
            MessageDetailScreen(
                conversationId = messageId,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToProduct = { productId ->
                    navController.navigate("product/$productId")
                },
                onNavigateToSettings = { shopName, shopAvatar ->
                    navController.navigate("message_setting/$shopName/$shopAvatar")
                }
            )
        }
        
        composable("message_setting/{shopName}/{shopAvatar}") { backStackEntry ->
            val shopName = backStackEntry.arguments?.getString("shopName") ?: ""
            val shopAvatar = backStackEntry.arguments?.getString("shopAvatar") ?: ""
            MessageSettingScreen(
                shopName = shopName,
                shopAvatar = shopAvatar,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToShop = {
                    navController.navigate("shop_page/$shopName")
                }
            )
        }
        
        composable("shop_page/{shopName}") { backStackEntry ->
            val shopName = backStackEntry.arguments?.getString("shopName") ?: "店铺主页"
            ShopPageScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onProductClick = { productId ->
                    navController.navigate("product_detail/$productId")
                },
                onCartClick = {
                    navController.navigate("shopping_cart")
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
            val orderType = backStackEntry.arguments?.getString("orderType") ?: "all"
            OrderScreen(
                orderType = orderType,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToPayment = { orderId ->
                    navController.navigate("order_confirm?fromOrder=$orderId")
                }
            )
        }
        
        composable("order_confirm?fromCart={fromCart}&fromOrder={fromOrder}") { backStackEntry ->
            val fromCart = backStackEntry.arguments?.getString("fromCart")?.toBooleanStrictOrNull() ?: false
            val fromOrder = backStackEntry.arguments?.getString("fromOrder")
            SettleScreen(
                fromCart = fromCart,
                fromOrder = fromOrder,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToPaymentSuccess = { orderAmount ->
                    navController.navigate("payment_success/$orderAmount")
                }
            )
        }
        
        composable("payment_success/{orderAmount}") { backStackEntry ->
            val orderAmount = backStackEntry.arguments?.getString("orderAmount") ?: "¥0.00"
            PaymentSuccessScreen(
                orderAmount = orderAmount,
                onViewOrderClick = {
                    navController.navigate("order_list/all") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onBackToHomeClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
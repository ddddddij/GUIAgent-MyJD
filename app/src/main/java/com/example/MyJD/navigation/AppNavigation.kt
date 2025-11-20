package com.example.MyJD.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.MyJD.repository.DataRepository
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
import com.example.MyJD.ui.screen.AddressListScreen
import com.example.MyJD.ui.screen.AddressDetailScreen
import com.example.MyJD.ui.screen.HuaweiP60DetailScreen
import com.example.MyJD.ui.screen.HuaweiMate60DetailScreen
import com.example.MyJD.ui.screen.HuaweiNova11DetailScreen
import com.example.MyJD.ui.screen.ThinkPadDetailScreen

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
                    // 根据产品ID进行条件路由
                    when {
                        productId.contains("huawei_p60") || productId.contains("华为P60") || productId.contains("P60") -> 
                            navController.navigate("huawei_p60_detail/$productId")
                        productId.contains("huawei_mate60") || productId.contains("华为Mate60") || productId.contains("Mate60") || productId.contains("mate60") -> 
                            navController.navigate("huawei_mate60_detail/$productId")
                        productId.contains("huawei_nova11") || productId.contains("华为Nova11") || productId.contains("Nova11") || productId.contains("nova11") -> 
                            navController.navigate("huawei_nova11_detail/$productId")
                        productId.contains("thinkpad") || productId.contains("ThinkPad") || productId.contains("联想ThinkPad") || productId.contains("联想笔记本") -> 
                            navController.navigate("thinkpad_detail/$productId")
                        productId.contains("iphone15") || productId.contains("iPhone15") -> 
                            navController.navigate("product/$productId")
                        else -> 
                            navController.navigate("product/$productId")
                    }
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
                    // 根据产品ID进行条件路由
                    when {
                        productId.contains("huawei_p60") || productId.contains("华为P60") || productId.contains("P60") -> 
                            navController.navigate("huawei_p60_detail/$productId")
                        productId.contains("huawei_mate60") || productId.contains("华为Mate60") || productId.contains("Mate60") || productId.contains("mate60") -> 
                            navController.navigate("huawei_mate60_detail/$productId")
                        productId.contains("iphone15") || productId.contains("iPhone15") -> 
                            navController.navigate("product/$productId")
                        else -> 
                            navController.navigate("product/$productId")
                    }
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
                },
                onShopClick = { shopName ->
                    navController.navigate("shop_page/$shopName")
                }
            )
        }
        
        composable("huawei_p60_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            HuaweiP60DetailScreen(
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
        
        composable("huawei_mate60_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            HuaweiMate60DetailScreen(
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
        
        composable("huawei_nova11_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            HuaweiNova11DetailScreen(
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
        
        composable("thinkpad_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ThinkPadDetailScreen(
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
                    // 根据产品ID进行条件路由
                    when {
                        productId.contains("huawei_p60") || productId.contains("华为P60") || productId.contains("P60") -> 
                            navController.navigate("huawei_p60_detail/$productId")
                        productId.contains("huawei_mate60") || productId.contains("华为Mate60") || productId.contains("Mate60") || productId.contains("mate60") -> 
                            navController.navigate("huawei_mate60_detail/$productId")
                        productId.contains("huawei_nova11") || productId.contains("华为Nova11") || productId.contains("Nova11") || productId.contains("nova11") -> 
                            navController.navigate("huawei_nova11_detail/$productId")
                        productId.contains("thinkpad") || productId.contains("ThinkPad") || productId.contains("联想ThinkPad") || productId.contains("联想笔记本") -> 
                            navController.navigate("thinkpad_detail/$productId")
                        productId.contains("iphone15") || productId.contains("iPhone15") -> 
                            navController.navigate("product/$productId")
                        else -> 
                            navController.navigate("product/$productId")
                    }
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
                    navController.navigate("product/$productId")
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
        
        composable("address?refresh={refresh}") { backStackEntry ->
            val refresh = backStackEntry.arguments?.getString("refresh")?.toBooleanStrictOrNull() ?: false
            AddressListScreen(
                refresh = refresh,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToAddressDetail = { addressId ->
                    navController.navigate("address_detail?addressId=$addressId")
                }
            )
        }
        
        composable("address_detail?addressId={addressId}") { backStackEntry ->
            val addressId = backStackEntry.arguments?.getString("addressId")?.takeIf { it != "null" }
            AddressDetailScreen(
                addressId = addressId,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveSuccess = {
                    navController.navigate("address?refresh=true") {
                        popUpTo("address") { inclusive = true }
                    }
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
        
        composable("order_confirm?fromCart={fromCart}&fromOrder={fromOrder}&selectedAddressId={selectedAddressId}") { backStackEntry ->
            val fromCart = backStackEntry.arguments?.getString("fromCart")?.toBooleanStrictOrNull() ?: false
            val fromOrder = backStackEntry.arguments?.getString("fromOrder")
            val selectedAddressId = backStackEntry.arguments?.getString("selectedAddressId")?.takeIf { it != "null" }
            
            // Load selected address if provided
            val repository = DataRepository.getInstance(LocalContext.current)
            var selectedAddress by remember { mutableStateOf<com.example.MyJD.model.Address?>(null) }
            
            LaunchedEffect(selectedAddressId) {
                selectedAddressId?.let { addressId ->
                    selectedAddress = repository.getAddressById(addressId)
                }
            }
            
            SettleScreen(
                fromCart = fromCart,
                fromOrder = fromOrder,
                selectedAddress = selectedAddress,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToPaymentSuccess = { orderAmount ->
                    navController.navigate("payment_success/$orderAmount")
                },
                onNavigateToAddressList = {
                    navController.navigate("address_from_settle")
                }
            )
        }
        
        composable("address_from_settle") {
            AddressListScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToAddressDetail = { addressId ->
                    navController.navigate("address_detail?addressId=$addressId")
                },
                onNavigateToSettleScreen = { selectedAddress ->
                    navController.navigate("order_confirm?selectedAddressId=${selectedAddress.id}") {
                        popUpTo("order_confirm") { inclusive = true }
                    }
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
package com.example.jd_sim.ui.navigation

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
import com.example.jd_sim.ui.screen.home.HomeScreen
import com.example.jd_sim.ui.screen.chat.ChatScreen
import com.example.jd_sim.ui.screen.me.MeScreen
import com.example.jd_sim.ui.screen.PlaceholderScreen
import com.example.jd_sim.ui.screen.productdetail.ProductDetailScreen
import com.example.jd_sim.ui.screen.order.OrderScreen
import com.example.jd_sim.ui.screen.settle.SettleScreen
import com.example.jd_sim.ui.screen.PaymentSuccessScreen
import com.example.jd_sim.ui.screen.cart.CartScreen
import com.example.jd_sim.ui.screen.search.SearchScreen
import com.example.jd_sim.ui.screen.searchresult.SearchResultScreen
import com.example.jd_sim.ui.screen.messagedetail.MessageDetailScreen
import com.example.jd_sim.ui.screen.messagesetting.MessageSettingScreen
import com.example.jd_sim.ui.screen.shop.ShopScreen
import com.example.jd_sim.ui.screen.addresslist.AddressListScreen
import com.example.jd_sim.ui.screen.addressdetail.AddressDetailScreen
import com.example.jd_sim.ui.screen.HuaweiP60DetailScreen
import com.example.jd_sim.ui.screen.HuaweiMate60DetailScreen
import com.example.jd_sim.ui.screen.HuaweiNova11DetailScreen
// import com.example.jd_sim.ui.screen.ThinkPadDetailScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToSearch = { query ->
                    val searchQuery = if (query.isBlank()) "__all__" else query
                    navController.navigate("search_result/$searchQuery")
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
                            navController.navigate("product/$productId")
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
                },
                onNavigateToFunction = { route ->
                    navController.navigate(route)
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
            CartScreen(
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
                },
                onNavigateToPlaceholder = { title ->
                    navController.navigate("placeholder/$title")
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
                onNavigateToProduct = { productId, searchKeyword ->
                    // 根据产品ID进行条件路由
                    when {
                        productId.contains("huawei_p60") || productId.contains("华为P60") || productId.contains("P60") ->
                            navController.navigate("huawei_p60_detail/$productId?searchKeyword=$searchKeyword")
                        productId.contains("huawei_mate60") || productId.contains("华为Mate60") || productId.contains("Mate60") || productId.contains("mate60") ->
                            navController.navigate("huawei_mate60_detail/$productId?searchKeyword=$searchKeyword")
                        productId.contains("iphone15") || productId.contains("iPhone15") ->
                            navController.navigate("product/$productId?searchKeyword=$searchKeyword")
                        else ->
                            navController.navigate("product/$productId?searchKeyword=$searchKeyword")
                    }
                },
                onNavigateToShop = { shopName ->
                    navController.navigate("shop_page/$shopName")
                }
            )
        }
        
        composable("product/{productId}?searchKeyword={searchKeyword}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val searchKeyword = backStackEntry.arguments?.getString("searchKeyword") ?: ""
            ProductDetailScreen(
                productId = productId,
                searchKeyword = searchKeyword,
                onBackClick = {
                    navController.popBackStack()
                },
                onCartClick = {
                    navController.navigate("cart")
                },
                onBuyNowClick = { orderId ->
                    navController.navigate("order_confirm?fromOrder=$orderId")
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
                        onBuyNowClick = { orderId ->
                            navController.navigate("order_confirm?fromOrder=$orderId")
                        },
                        onShopClick = { shopName ->
                            navController.navigate("shop_page/$shopName")
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
        
                                onBuyNowClick = { orderId ->
        
                                    navController.navigate("order_confirm?fromOrder=$orderId")
        
                                },
                                onShopClick = { shopName ->
                                    navController.navigate("shop_page/$shopName")
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
        
                                onBuyNowClick = { orderId ->
        
                                    navController.navigate("order_confirm?fromOrder=$orderId")
        
                                },
                                onShopClick = { shopName ->
                                    navController.navigate("shop_page/$shopName")
                                }
        
                            )
        
                        }
        
        // ThinkPad now uses standard ProductDetailScreen via "product/{productId}" route
        // composable("thinkpad_detail/{productId}") { backStackEntry ->
        //     val productId = backStackEntry.arguments?.getString("productId") ?: ""
        //     ThinkPadDetailScreen(
        //         productId = productId,
        //         onBackClick = {
        //             navController.popBackStack()
        //         },
        //         onCartClick = {
        //             navController.navigate("cart")
        //         },
        //         onBuyNowClick = {
        //             navController.navigate("order_confirm")
        //         }
        //     )
        // }
        
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
                            navController.navigate("product/$productId")
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
            ShopScreen(
                shopName = shopName,
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

        // 首页功能入口占位页面
        composable("seckill") {
            PlaceholderScreen(
                title = "秒杀",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("trial") {
            PlaceholderScreen(
                title = "试用领取",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("coupon") {
            PlaceholderScreen(
                title = "领券",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("hotel") {
            PlaceholderScreen(
                title = "酒店",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("fashion") {
            PlaceholderScreen(
                title = "服饰鞋包",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("phone") {
            PlaceholderScreen(
                title = "手机",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("digital") {
            PlaceholderScreen(
                title = "数码",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("appliance") {
            PlaceholderScreen(
                title = "家电",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("more") {
            PlaceholderScreen(
                title = "更多",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // 我的页面功能入口占位页面
        composable("placeholder/{title}") { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: "功能开发中"
            PlaceholderScreen(
                title = title,
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

            SettleScreen(
                fromCart = fromCart,
                fromOrder = fromOrder,
                selectedAddressId = selectedAddressId,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToPaymentSuccess = { orderAmount ->
                    navController.navigate("payment_success/$orderAmount")
                },
                onNavigateToAddressList = {
                    val routeParams = buildString {
                        append("address_from_settle")
                        val params = mutableListOf<String>()
                        if (fromCart) {
                            params.add("fromCart=true")
                        }
                        if (fromOrder != null && fromOrder != "null") {
                            params.add("fromOrder=$fromOrder")
                        }
                        if (params.isNotEmpty()) {
                            append("?${params.joinToString("&")}")
                        }
                    }
                    navController.navigate(routeParams)
                }
            )
        }
        
        composable("address_from_settle?fromCart={fromCart}&fromOrder={fromOrder}") { backStackEntry ->
            val fromCart = backStackEntry.arguments?.getString("fromCart")?.toBooleanStrictOrNull() ?: false
            val fromOrder = backStackEntry.arguments?.getString("fromOrder")
            
            AddressListScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToAddressDetail = { addressId ->
                    navController.navigate("address_detail?addressId=$addressId")
                },
                onNavigateToSettleScreen = { selectedAddress ->
                    val routeParams = buildString {
                        append("order_confirm?selectedAddressId=${selectedAddress.id}")
                        val params = mutableListOf<String>()
                        if (fromCart) {
                            params.add("fromCart=true")
                        }
                        if (fromOrder != null && fromOrder != "null") {
                            params.add("fromOrder=$fromOrder")
                        }
                        if (params.isNotEmpty()) {
                            append("&${params.joinToString("&")}")
                        }
                    }
                    navController.navigate(routeParams) {
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
package com.example.jd_sim.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jd_sim.ui.screen.home.HomeViewModel
import com.example.jd_sim.ui.components.HomeHeader
import com.example.jd_sim.ui.components.BannerSection
import com.example.jd_sim.ui.components.FunctionGrid
import com.example.jd_sim.ui.components.RecommendSection

@Composable
fun HomeScreen(
    onNavigateToSearch: (String) -> Unit = {},
    onNavigateToProduct: (String) -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToSupermarket: () -> Unit = {},
    onNavigateToFunction: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    
    val banners by viewModel.banners.collectAsState()
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 确保在页面初始化时立即加载数据
    LaunchedEffect(Unit) {
        if (products.isEmpty()) {
            viewModel.refreshData()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7)),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            HomeHeader(
                onSearchClick = { query -> onNavigateToSearch(query) },
                onCartClick = onNavigateToCart
            )
        }
        
        item {
            BannerSection(
                banners = banners,
                products = products,
                onBannerClick = { banner ->
                    when (banner.type) {
                        "PRODUCT" -> {
                            val productId = banner.actionUrl.removePrefix("/products/")
                            onNavigateToProduct(productId)
                        }
                        "CATEGORY" -> {
                            if (banner.actionUrl == "/supermarket") {
                                onNavigateToSupermarket()
                            }
                        }
                        else -> {
                            // Handle other banner types or show placeholder
                        }
                    }
                },
                onProductClick = onNavigateToProduct
            )
        }
        
        item {
            FunctionGrid(
                onFunctionClick = { function ->
                    when (function) {
                        "京东超市" -> onNavigateToSupermarket()
                        "秒杀" -> onNavigateToFunction("seckill")
                        "试用领取" -> onNavigateToFunction("trial")
                        "领券" -> onNavigateToFunction("coupon")
                        "酒店" -> onNavigateToFunction("hotel")
                        "服饰鞋包" -> onNavigateToFunction("fashion")
                        "手机" -> onNavigateToFunction("phone")
                        "数码" -> onNavigateToFunction("digital")
                        "家电" -> onNavigateToFunction("appliance")
                        "更多" -> onNavigateToFunction("more")
                        else -> onNavigateToFunction("more")
                    }
                }
            )
        }
        
        item {
            RecommendSection(
                products = products,
                onProductClick = onNavigateToProduct,
                onAddToCart = { product ->
                    viewModel.addToCart(
                        product = product,
                        selectedColor = product.selectedColor ?: "",
                        selectedVersion = product.selectedVersion ?: ""
                    )
                }
            )
        }
    }
}

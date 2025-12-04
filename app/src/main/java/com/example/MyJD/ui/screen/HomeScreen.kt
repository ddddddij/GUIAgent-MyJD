package com.example.myjd.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myjd.viewmodel.HomeViewModel
import com.example.myjd.ui.components.HomeHeader
import com.example.myjd.ui.components.BannerSection
import com.example.myjd.ui.components.FunctionGrid
import com.example.myjd.ui.components.RecommendSection

@Composable
fun HomeScreen(
    onNavigateToSearch: (String) -> Unit = {},
    onNavigateToProduct: (String) -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToSupermarket: () -> Unit = {},
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
        modifier = Modifier.fillMaxSize(),
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
                }
            )
        }
        
        item {
            FunctionGrid(
                onFunctionClick = { function ->
                    when (function) {
                        "京东超市" -> onNavigateToSupermarket()
                        else -> {
                            // Navigate to placeholder for other functions
                        }
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
package com.example.MyJD.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.viewmodel.HomeViewModel
import com.example.MyJD.ui.components.HomeHeader
import com.example.MyJD.ui.components.BannerSection
import com.example.MyJD.ui.components.FunctionGrid
import com.example.MyJD.ui.components.RecommendSection

@Composable
fun HomeScreen(
    onNavigateToSearch: (String) -> Unit = {},
    onNavigateToProduct: (String) -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToSupermarket: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val viewModel: HomeViewModel = viewModel(
        factory = com.example.MyJD.viewmodel.ViewModelFactory(repository, context)
    )
    
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
                products = products.take(10), // 直接使用状态中的products而不是调用方法
                onProductClick = onNavigateToProduct,
                onAddToCart = { product ->
                    viewModel.addToCart(product)
                }
            )
        }
    }
}
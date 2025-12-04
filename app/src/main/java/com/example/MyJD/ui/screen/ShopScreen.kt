package com.example.myjd.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myjd.domain.model.ShopPageData
import com.example.myjd.domain.model.ShopCategory
import com.example.myjd.domain.model.Product
import com.example.myjd.ui.components.ProductCardItem

import com.example.myjd.domain.model.ShopStatistic
import com.example.myjd.viewmodel.ShopViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myjd.domain.model.ShopInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    shopName: String,
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit = {},
    onCartClick: () -> Unit = {},
    viewModel: ShopViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val shopData by viewModel.shopData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    LaunchedEffect(key1 = shopName) {
        viewModel.initialize(shopName)
    }

    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { event ->
            when (val eventType = event.type) {
                is ShopViewModel.NavigationType.Back -> onBackClick()
                is ShopViewModel.NavigationType.ToProductDetail -> {
                    onProductClick(eventType.productId)
                }
            }
            viewModel.clearNavigationEvent()
        }
    }

    toastMessage?.let { message ->
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = shopData?.shopInfo?.name ?: shopName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onBackClick() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).background(Color(0xFFF5F5F5))) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFE53935))
                }
            } else {
                shopData?.let { data ->
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        ShopHeaderSection(shopInfo = data.shopInfo, onFollowClick = { /* TODO */ })
                        ServiceBannerSection(banner = data.shopInfo.serviceBanner)
                        StatisticsSection(statistics = data.statistics)
                        CategoryTabsSection(
                            categories = data.categories,
                            onCategoryClick = { viewModel.onCategorySelected(it) }
                        )
                        ProductGridSection(
                            products = data.products,
                            onProductClick = { viewModel.onProductClick(it) },
                            onAddToCartClick = { viewModel.onAddToCartClick(it) }
                        )
                    }
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "😕", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "店铺数据加载失败", fontSize = 16.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShopHeaderSection(shopInfo: ShopInfo, onFollowClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/${shopInfo.avatar}")
                .build(),
            contentDescription = "店铺头像",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = shopInfo.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "粉丝: ${shopInfo.followers}", color = Color.Gray, fontSize = 14.sp)
        }
        Button(
            onClick = onFollowClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = if (shopInfo.isFollowed) Color.Gray else Color(0xFFE53935))
        ) {
            Text(text = if (shopInfo.isFollowed) "已关注" else "+ 关注")
        }
    }
}

@Composable
fun ServiceBannerSection(banner: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = banner,
            fontSize = 12.sp,
            color = Color.DarkGray,
            modifier = Modifier
                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatisticsSection(statistics: List<ShopStatistic>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        statistics.forEach { statistic ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = statistic.value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = statistic.label, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun CategoryTabsSection(categories: List<ShopCategory>, onCategoryClick: (String) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            Text(
                text = category.name,
                modifier = Modifier
                    .clickable { onCategoryClick(category.id) }
                    .background(
                        if (category.isSelected) Color(0xFFFFEBEE) else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = if (category.isSelected) Color(0xFFE53935) else Color.Black,
                fontWeight = if (category.isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ProductGridSection(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onAddToCartClick: (Product) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 1000.dp) // Avoid nested scrolling issues
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCardItem(
                product = product,
                onProductClick = { onProductClick(product) },
                onAddToCartClick = { onAddToCartClick(product) }
            )
        }
    }
}


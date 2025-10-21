package com.example.MyJD.ui.screen

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
import com.example.MyJD.model.ShopPageData
import com.example.MyJD.model.ShopCategory
import com.example.MyJD.model.Product
import com.example.MyJD.presenter.ShopPageContract
import com.example.MyJD.presenter.ShopPagePresenter
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.ui.components.ProductCardItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopPageScreen(
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = DataRepository.getInstance(context)
    val presenter = remember { ShopPagePresenter(repository) }
    
    var shopData by remember { mutableStateOf<ShopPageData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    
    val view = remember {
        object : ShopPageContract.View {
            override fun showShopData(data: ShopPageData) {
                shopData = data
            }
            
            override fun showLoading(show: Boolean) {
                isLoading = show
            }
            
            override fun showToast(message: String) {
                toastMessage = message
            }
            
            override fun updateCategories(categories: List<ShopCategory>) {
                shopData = shopData?.copy(categories = categories)
            }
            
            override fun navigateToProductDetail(productId: String) {
                onProductClick(productId)
            }
            
            override fun navigateBack() {
                onBackClick()
            }
        }
    }
    
    LaunchedEffect(Unit) {
        presenter.attach(view)
        presenter.loadShopData()
    }
    
    DisposableEffect(Unit) {
        onDispose {
            presenter.detach()
        }
    }
    
    // Show toast if there's a message
    toastMessage?.let { message ->
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(2000)
            toastMessage = null
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = shopData?.shopInfo?.name ?: "Apple产品京东自营旗舰店",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { presenter.onBackClick() }) {
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFFE53935)
                )
            }
        } else {
            shopData?.let { data ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 店铺头部信息
                    ShopHeaderSection(
                        shopInfo = data.shopInfo,
                        onFollowClick = { /* TODO: 关注功能 */ }
                    )
                    
                    // 服务标语
                    if (data.shopInfo.serviceBanner.isNotEmpty()) {
                        ServiceBannerSection(banner = data.shopInfo.serviceBanner)
                    }
                    
                    // 店铺统计信息
                    if (data.statistics.isNotEmpty()) {
                        StatisticsSection(statistics = data.statistics)
                    }
                    
                    // 分类标签
                    if (data.categories.isNotEmpty()) {
                        CategoryTabsSection(
                            categories = data.categories,
                            onCategoryClick = { categoryId ->
                                presenter.onCategorySelected(categoryId)
                            }
                        )
                    }
                    
                    // 商品网格
                    ProductGridSection(
                        products = data.products,
                        onProductClick = { product ->
                            presenter.onProductClick(product)
                        },
                        onAddToCartClick = { product ->
                            presenter.onAddToCartClick(product)
                        }
                    )
                }
            } ?: run {
                // 数据加载失败时的占位UI
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "😕",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "店铺数据加载失败",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
        
        // Toast显示
        toastMessage?.let { message ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    modifier = Modifier
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = message,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ShopHeaderSection(
    shopInfo: com.example.MyJD.model.ShopInfo,
    onFollowClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 店铺头像
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        Color(0xFFF5F5F5),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = shopInfo.avatar,
                    fontSize = 32.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 店铺信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = shopInfo.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "粉丝 ${shopInfo.followers}",
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }
            
            // 关注按钮
            Button(
                onClick = onFollowClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (shopInfo.isFollowed) Color(0xFFF5F5F5) else Color(0xFFE53935)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(
                    text = if (shopInfo.isFollowed) "已关注" else "+ 关注",
                    fontSize = 12.sp,
                    color = if (shopInfo.isFollowed) Color(0xFF666666) else Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ServiceBannerSection(banner: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📋",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = banner,
                fontSize = 13.sp,
                color = Color(0xFF333333),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatisticsSection(statistics: List<com.example.MyJD.model.ShopStatistic>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        LazyRow(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items(statistics) { stat ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(80.dp)
                ) {
                    Text(
                        text = stat.icon ?: "📊",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stat.value,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stat.label,
                        fontSize = 10.sp,
                        color = Color(0xFF666666),
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryTabsSection(
    categories: List<ShopCategory>,
    onCategoryClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                onClick = { onCategoryClick(category.id) },
                label = {
                    Text(
                        text = category.name,
                        fontSize = 12.sp,
                        fontWeight = if (category.isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = category.isSelected,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFE53935),
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFFF5F5F5),
                    labelColor = Color(0xFF666666)
                )
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
            .padding(horizontal = 16.dp)
            .height(800.dp), // 固定高度避免嵌套滚动冲突
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products) { product ->
            ProductCardItem(
                product = product,
                onProductClick = onProductClick,
                onAddToCartClick = onAddToCartClick
            )
        }
    }
    
    // 添加底部间距
    Spacer(modifier = Modifier.height(16.dp))
}
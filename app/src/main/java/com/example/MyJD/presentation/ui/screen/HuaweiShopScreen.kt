package com.example.MyJD.presentation.ui.screen

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
import com.example.MyJD.presentation.model.ShopPageData
import com.example.MyJD.presentation.model.ShopCategory
import com.example.MyJD.presentation.model.ShopInfo
import com.example.MyJD.presentation.model.ShopStatistic
import com.example.MyJD.domain.model.Product
import com.example.MyJD.presentation.presenter.ShopPageContract
import com.example.MyJD.presentation.presenter.HuaweiShopPresenter
import com.example.MyJD.data.repository.DataRepository
import com.example.MyJD.presentation.ui.components.ProductCardItem
import com.example.MyJD.utils.TaskSeventeenLogger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HuaweiShopScreen(
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = DataRepository.getInstance(context)
    val presenter = remember { HuaweiShopPresenter(repository) }

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

        // 任务十七日志记录：加载店铺页面数据
        TaskSeventeenLogger.logShopPageDataLoading(context)
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
                        text = shopData?.shopInfo?.name ?: "华为官方旗舰店",
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
                            // 任务十七日志记录：选择iPhone 15 粉色 256GB
                            if (product.name.contains("iPhone 15") &&
                                product.name.contains("粉色") &&
                                product.name.contains("256GB")) {
                                TaskSeventeenLogger.logShopProductSelected(context, product.name, product.id)
                            }
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

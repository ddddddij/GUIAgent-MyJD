package com.example.jd_sim.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.jd_sim.domain.model.Product
import com.example.jd_sim.viewmodel.SearchSortType
import com.example.jd_sim.ui.components.FilterBottomSheet
import com.example.jd_sim.ui.theme.JDRed
import com.example.jd_sim.viewmodel.SearchFilter
import com.example.jd_sim.viewmodel.SearchResultViewModel
import com.example.jd_sim.viewmodel.SearchTabType
import com.example.jd_sim.viewmodel.ShopItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    keyword: String,
    onBackClick: () -> Unit = {},
    onNavigateToProduct: (String) -> Unit = {},
    onNavigateToShop: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SearchResultViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(keyword) {
        viewModel.loadSearchResults(keyword)
    }

    val products by viewModel.products.collectAsState()
    val shops by viewModel.shops.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentSortType by viewModel.currentSortType.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    val currentFilter by viewModel.currentFilter.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部搜索栏
        SearchResultTopBar(
            searchKeyword = keyword,
            onBackClick = onBackClick,
            onSearchClick = { viewModel.loadSearchResults(keyword) },
            onKeywordChange = { /* Implement if search keyword can be changed on this screen */ }
        )

        // Tab切换栏
        SearchTabBar(
            currentTab = currentTab,
            onTabClick = { viewModel.switchTab(it) }
        )

        // 筛选排序栏 - 仅在商品Tab显示
        if (currentTab == SearchTabType.PRODUCTS) {
            SortAndFilterBar(
                currentSortType = currentSortType,
                onSortClick = { viewModel.sortProducts(it) },
                onFilterClick = { showFilterDialog = true }
            )
        }

        // 内容区域
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = JDRed)
            }
        } else {
            when (currentTab) {
                SearchTabType.PRODUCTS -> {
                    // 商品列表
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(products) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onNavigateToProduct(product.id) }
                            )
                        }
                    }
                }
                SearchTabType.SHOPS -> {
                    // 店铺列表
                    if (shops.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "未找到相关店铺",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(shops.size) { index ->
                                ShopCard(
                                    shop = shops[index],
                                    onClick = { onNavigateToShop(shops[index].name) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 筛选弹窗
    if (showFilterDialog) {
        FilterBottomSheet(
            currentFilter = currentFilter,
            onApplyFilter = { filter ->
                viewModel.filterProducts(filter)
                showFilterDialog = false
            },
            onResetFilter = {
                viewModel.resetFilter()
            },
            onDismiss = {
                showFilterDialog = false
            }
        )
    }
}

@Composable
private fun SearchResultTopBar(
    searchKeyword: String,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onKeywordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFFFF6600),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 返回按钮
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.White
                )
            }
            
            // 搜索框
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(18.dp)
                    )
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = searchKeyword,
                    onValueChange = onKeywordChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 搜索按钮
            Button(
                onClick = onSearchClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = JDRed
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(
                    text = "搜索",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SortAndFilterBar(
    currentSortType: SearchSortType,
    onSortClick: (SearchSortType) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 综合
            SortButton(
                text = "综合",
                isSelected = currentSortType == SearchSortType.COMPREHENSIVE,
                onClick = { onSortClick(SearchSortType.COMPREHENSIVE) }
            )
            
            // 销量
            SortButton(
                text = "销量",
                isSelected = currentSortType == SearchSortType.SALES,
                onClick = { onSortClick(SearchSortType.SALES) }
            )
            
            // 价格
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    val nextSortType = if (currentSortType == SearchSortType.PRICE_ASC) {
                        SearchSortType.PRICE_DESC
                    } else {
                        SearchSortType.PRICE_ASC
                    }
                    onSortClick(nextSortType)
                }
            ) {
                Text(
                    text = "价格",
                    color = if (currentSortType == SearchSortType.PRICE_ASC || currentSortType == SearchSortType.PRICE_DESC) 
                        JDRed else Color.Gray,
                    fontSize = 14.sp
                )
                
                Column {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        tint = if (currentSortType == SearchSortType.PRICE_ASC) JDRed else Color.Gray,
                        modifier = Modifier.size(12.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = if (currentSortType == SearchSortType.PRICE_DESC) JDRed else Color.Gray,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            
            // 筛选
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onFilterClick() }
            ) {
                Text(
                    text = "筛选",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SortButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            color = if (isSelected) JDRed else Color.Gray,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(2.dp)
                    .background(JDRed)
            )
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // 商品图片
            AsyncImage(
                model = "file:///android_asset/${product.imageUrl}",
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 商品名称
            Text(
                text = product.name,
                fontSize = 14.sp,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 商品价格
            Text(
                text = "¥${product.price}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = JDRed
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 店铺名称
            Text(
                text = product.storeName,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ModalBottomSheetLayout(
    sheetContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()

        // 半透明背景
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        // 底部弹窗内容
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        ) {
            sheetContent()
        }
    }
}

@Composable
private fun SearchTabBar(
    currentTab: SearchTabType,
    onTabClick: (SearchTabType) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 0.dp)
        ) {
            // 商品Tab
            TabItem(
                text = "商品",
                isSelected = currentTab == SearchTabType.PRODUCTS,
                onClick = { onTabClick(SearchTabType.PRODUCTS) },
                modifier = Modifier.weight(1f)
            )

            // 店铺Tab
            TabItem(
                text = "店铺",
                isSelected = currentTab == SearchTabType.SHOPS,
                onClick = { onTabClick(SearchTabType.SHOPS) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            color = if (isSelected) JDRed else Color.Gray,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .height(3.dp)
                    .background(JDRed, RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
private fun ShopCard(
    shop: ShopItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 店铺logo
            AsyncImage(
                model = coil.request.ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${shop.avatar}")
                    .build(),
                contentDescription = shop.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 店铺名称
                Text(
                    text = shop.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 店铺描述
                Text(
                    text = shop.description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 进入店铺按钮
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "进入店铺",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

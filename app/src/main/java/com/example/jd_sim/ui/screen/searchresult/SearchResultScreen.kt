package com.example.jd_sim.ui.screen.searchresult

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jd_sim.ui.components.FilterBottomSheet
import com.example.jd_sim.ui.theme.JDRed
import com.example.jd_sim.ui.screen.searchresult.components.*
import com.example.jd_sim.ui.screen.searchresult.SearchResultViewModel
import com.example.jd_sim.ui.screen.searchresult.SearchTabType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    keyword: String,
    onBackClick: () -> Unit = {},
    onNavigateToProduct: (String, String) -> Unit = { _, _ -> },
    onNavigateToShop: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SearchResultViewModel = hiltViewModel()
) {
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
            SearchSortAndFilterBar(
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
                            SearchProductCard(
                                product = product,
                                onClick = { onNavigateToProduct(product.id, keyword) }
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
                                SearchShopCard(
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

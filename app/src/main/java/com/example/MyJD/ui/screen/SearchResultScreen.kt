package com.example.MyJD.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.MyJD.model.Product
import com.example.MyJD.presenter.SearchSortType
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.ui.components.FilterBottomSheet
import com.example.MyJD.ui.theme.JDRed
import com.example.MyJD.viewmodel.SearchResultViewModel
import com.example.MyJD.viewmodel.SearchResultViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    keyword: String,
    onBackClick: () -> Unit = {},
    onNavigateToProduct: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val viewModel: SearchResultViewModel = viewModel(
        factory = SearchResultViewModelFactory(repository, keyword, context)
    )
    
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val currentSortType by viewModel.currentSortType.collectAsState()
    val showFilterDialog by viewModel.showFilterDialog.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()
    val searchKeyword by viewModel.searchKeyword.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    
    // 处理Toast消息
    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }
    
    // 处理导航事件
    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { productId ->
            onNavigateToProduct(productId)
            viewModel.clearNavigationEvent()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部搜索栏
        SearchResultTopBar(
            searchKeyword = searchKeyword,
            onBackClick = onBackClick,
            onSearchClick = { viewModel.onSearchClicked() },
            onKeywordChange = { viewModel.updateSearchKeyword(it) }
        )
        
        // 筛选排序栏
        SortAndFilterBar(
            currentSortType = currentSortType,
            onSortClick = { viewModel.onSortClicked(it) },
            onFilterClick = { viewModel.onFilterClicked() }
        )
        
        // 商品列表
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = JDRed)
            }
        } else {
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
                        onClick = { viewModel.onProductClicked(product.id) }
                    )
                }
            }
        }
    }
    
    // 筛选弹窗
    if (showFilterDialog) {
        ModalBottomSheetLayout(
            sheetContent = {
                FilterBottomSheet(
                    currentFilter = currentFilter,
                    onApplyFilter = { filter ->
                        viewModel.applyFilter(filter)
                    },
                    onResetFilter = {
                        viewModel.resetFilter()
                    },
                    onDismiss = {
                        viewModel.dismissFilterDialog()
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {}
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
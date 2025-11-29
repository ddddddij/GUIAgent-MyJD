package com.example.MyJD.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.MyJD.model.CartItem
import com.example.MyJD.model.CartItemSpec
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.viewmodel.HomeViewModel
import com.example.MyJD.viewmodel.ViewModelFactory
import com.example.MyJD.ui.theme.JDRed
import com.example.MyJD.ui.theme.JDTextPrimary
import com.example.MyJD.ui.theme.JDTextSecondary
import com.example.MyJD.ui.components.CartHeader
import com.example.MyJD.ui.components.CartTabs
import com.example.MyJD.ui.components.CartStoreSection
import com.example.MyJD.ui.components.CartProductCard
import com.example.MyJD.utils.TaskNineLogger
import com.example.MyJD.utils.TaskSixteenLogger
import android.widget.Toast

@Composable
fun CartScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToCheckout: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory(repository, context)
    )
    
    // 使用StateFlow响应式获取购物车数据
    val specCartItems by repository.specCartFlow.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf("全部") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CartItemSpec?>(null) }

    // 计算是否全选
    val allSelected = remember(specCartItems) {
        repository.isAllSpecCartSelected()
    }
    
    // 任务九日志记录：进入购物车页面和数据加载
    LaunchedEffect(Unit) {
        TaskNineLogger.logTaskStart(context)
        TaskNineLogger.logCartPageEntered(context)
        
        // 强制加载购物车数据（如果为空）
        if (specCartItems.isEmpty()) {
            repository.forceLoadCartDataFromAssets()
        }
    }
    
    // 日志记录购物车数据变化
    LaunchedEffect(specCartItems) {
        android.util.Log.d("CartScreen", "Cart data updated via StateFlow: ${specCartItems.size} items")
        
        // 任务九日志记录：商品加载和总价计算
        TaskNineLogger.logCartItemsLoaded(context, specCartItems.size)
        val calculatedTotalPrice = repository.getSelectedSpecCartTotalPrice()
        TaskNineLogger.logTotalPriceCalculated(context, calculatedTotalPrice)
        TaskNineLogger.logTaskCompleted(context, calculatedTotalPrice)
        
        // 任务十六日志记录：检查购物车中是否有iPhone15商品
        val hasIphone15 = specCartItems.any { item ->
            item.productName.contains("iPhone15") || item.productName.contains("iPhone 15")
        }
        if (hasIphone15) {
            val iphone15Items = specCartItems.filter { item ->
                (item.productName.contains("iPhone15") || item.productName.contains("iPhone 15")) && 
                item.getSpecText().contains("黑色") && item.getSpecText().contains("256GB")
            }
            if (iphone15Items.isNotEmpty()) {
                TaskSixteenLogger.logAddToCartSuccess(context, "iPhone15 黑色 256GB", iphone15Items.first().quantity)
                TaskSixteenLogger.logCartPageEntered(context, specCartItems.size)
            }
        }
    }
    
    // StateFlow会自动更新，无需手动刷新函数
    
    val totalCount = repository.getSpecCartTotalCount()
    val selectedCount = repository.getSelectedSpecCartCount()
    val totalPrice = repository.getSelectedSpecCartTotalPrice()

    if (showDeleteDialog && itemToDelete != null) {
        DeleteConfirmationDialog(
            cartItem = itemToDelete!!,
            onConfirm = {
                repository.removeFromSpecCart(itemToDelete!!.id)
                showDeleteDialog = false
                itemToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                itemToDelete = null
            }
        )
    }

    Scaffold(
        topBar = {
            CartHeader(
                cartCount = totalCount,
                onLocationClick = {
                    Toast.makeText(context, "地址管理功能开发中", Toast.LENGTH_SHORT).show()
                },
                onManageClick = {
                    Toast.makeText(context, "管理功能开发中", Toast.LENGTH_SHORT).show()
                },
                onMoreClick = {
                    Toast.makeText(context, "更多功能开发中", Toast.LENGTH_SHORT).show()
                }
            )
        },
        bottomBar = {
            if (specCartItems.isNotEmpty()) {
                NewCartBottomBar(
                    isAllSelected = allSelected,
                    onAllSelectToggle = {
                        repository.toggleAllSpecCartSelection()
                    },
                    selectedCount = selectedCount,
                    totalPrice = totalPrice,
                    onCheckout = {
                        if (selectedCount > 0) {
                            onNavigateToCheckout()
                        } else {
                            Toast.makeText(context, "请选择要结算的商品", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        if (specCartItems.isEmpty()) {
            EmptyCartContent(
                onNavigateToHome = onNavigateToHome,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
            ) {
                item {
                    // 标签页
                    CartTabs(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
                
                // 按店铺分组显示商品
                val groupedItems = specCartItems.groupBy { it.storeName }
                groupedItems.forEach { (storeName, items) ->
                    item {
                        // 店铺信息
                        CartStoreSection(
                            storeName = storeName,
                            subsidyInfo = "政府补贴满1000减100",
                            isSelected = items.all { it.selected },
                            onSelectionToggle = {
                                repository.toggleStoreSpecCartSelection(storeName)
                            },
                            onCouponClick = {
                                Toast.makeText(context, "领券功能开发中", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    
                    items(items) { cartItem ->
                        CartProductCard(
                            cartItem = cartItem,
                            onSelectionToggle = {
                                repository.toggleSpecCartItemSelection(cartItem.id)
                            },
                            onQuantityChange = { newQuantity ->
                                repository.updateSpecCartItemQuantity(cartItem.id, newQuantity)
                            },
                            onSpecChange = {
                                Toast.makeText(context, "规格修改功能开发中", Toast.LENGTH_SHORT).show()
                            },
                            onRemove = {
                                itemToDelete = it
                                showDeleteDialog = true
                            }
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    cartItem: CartItemSpec,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("确认删除") },
        text = { Text("您确定要删除商品 '${cartItem.productName}' 吗？") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = JDRed)
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}


@Composable
private fun EmptyCartContent(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "空购物车",
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Text(
                text = "购物车还是空的",
                fontSize = 18.sp,
                color = JDTextSecondary
            )
            Text(
                text = "快去挑选心仪的商品吧",
                fontSize = 14.sp,
                color = JDTextSecondary
            )
            Button(
                onClick = onNavigateToHome,
                colors = ButtonDefaults.buttonColors(containerColor = JDRed)
            ) {
                Text("去逛逛", color = Color.White)
            }
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    onSelectionToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = cartItem.isSelected,
                onCheckedChange = { onSelectionToggle() },
                colors = CheckboxDefaults.colors(checkedColor = JDRed)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Product image placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📱", fontSize = 24.sp)
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.product.name,
                    fontSize = 14.sp,
                    color = JDTextPrimary,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "¥${cartItem.product.price}",
                    fontSize = 16.sp,
                    color = JDRed,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quantity controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.background(
                            Color(0xFFF5F5F5),
                            RoundedCornerShape(4.dp)
                        )
                    ) {
                        IconButton(
                            onClick = { 
                                if (cartItem.quantity > 1) {
                                    onQuantityChange(cartItem.quantity - 1)
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Filled.Remove,
                                contentDescription = "减少",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        Text(
                            text = "${cartItem.quantity}",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        
                        IconButton(
                            onClick = { onQuantityChange(cartItem.quantity + 1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "增加",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    IconButton(onClick = onRemove) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "删除",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartBottomBar(
    totalPrice: Double,
    selectedCount: Int,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "已选商品($selectedCount)件",
                    fontSize = 12.sp,
                    color = JDTextSecondary
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "合计: ¥",
                        fontSize = 14.sp,
                        color = JDTextPrimary
                    )
                    Text(
                        text = "${totalPrice.toInt()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = JDRed
                    )
                    val decimal = ((totalPrice - totalPrice.toInt()) * 100).toInt()
                    if (decimal > 0) {
                        Text(
                            text = ".${decimal}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = JDRed
                        )
                    }
                }
            }
            
            Button(
                onClick = onCheckout,
                enabled = selectedCount > 0,
                colors = ButtonDefaults.buttonColors(containerColor = JDRed),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "结算($selectedCount)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun NewCartBottomBar(
    isAllSelected: Boolean,
    onAllSelectToggle: () -> Unit,
    selectedCount: Int,
    totalPrice: Double,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 全选
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = isAllSelected,
                    onCheckedChange = { onAllSelectToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFE2231A),
                        uncheckedColor = Color(0xFFCCCCCC)
                    )
                )
                
                Text(
                    text = "全选",
                    fontSize = 14.sp,
                    color = Color(0xFF333333)
                )
            }
            
            // 合计和结算
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "合计：¥",
                            fontSize = 14.sp,
                            color = Color(0xFF333333)
                        )
                        Text(
                            text = "${totalPrice.toInt()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE2231A)
                        )
                        val decimal = ((totalPrice - totalPrice.toInt()) * 100).toInt()
                        if (decimal > 0) {
                            Text(
                                text = ".${String.format("%02d", decimal)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE2231A)
                            )
                        }
                    }
                }
                
                Button(
                    onClick = onCheckout,
                    enabled = selectedCount > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE2231A),
                        disabledContainerColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "去结算",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}